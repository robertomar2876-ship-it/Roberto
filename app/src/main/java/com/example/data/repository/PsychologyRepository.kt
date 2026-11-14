package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.local.SeedContent
import com.example.data.local.SeedDisorders
import com.example.data.model.ArticleEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.EducationalResourceEntity
import com.example.data.model.MentalDisorderEntity
import com.example.data.model.PhotoGalleryEntity
import com.example.data.model.PhotoItemEntity
import com.example.data.model.VideoItemEntity
import com.example.data.storage.MediaIngestionResult
import com.example.data.storage.MediaStorageManager
import com.example.data.storage.StorageUsageSummary
import kotlinx.coroutines.flow.Flow

class PsychologyRepository(
    context: Context,
    private val db: AppDatabase = AppDatabase.getInstance(context),
    val storageManager: MediaStorageManager = MediaStorageManager(context)
) {
    val disorders: Flow<List<MentalDisorderEntity>> = db.disorderDao().observeAll()
    val videos: Flow<List<VideoItemEntity>> = db.videoDao().observeAll()
    val galleries: Flow<List<PhotoGalleryEntity>> = db.galleryDao().observeAllGalleries()
    val photos: Flow<List<PhotoItemEntity>> = db.galleryDao().observeAllPhotos()
    val articles: Flow<List<ArticleEntity>> = db.articleDao().observeAll()
    val resources: Flow<List<EducationalResourceEntity>> = db.resourceDao().observeAll()
    val categories: Flow<List<CategoryEntity>> = db.categoryDao().observeAll()
    val contactMessages: Flow<List<ContactMessageEntity>> = db.contactMessageDao().observeAll()

    suspend fun ensureSeeded() {
        val now = System.currentTimeMillis()
        val expectedDisorders = SeedDisorders.all()
        if (db.disorderDao().count() != expectedDisorders.size) {
            db.disorderDao().replaceAll(expectedDisorders)
        }
        if (db.categoryDao().count() == 0) {
            db.categoryDao().insertAll(SeedContent.categories())
        }
        if (db.articleDao().count() == 0) {
            db.articleDao().insertAll(SeedContent.articles(now))
        }
        if (db.resourceDao().count() == 0) {
            val generatedResources = SeedContent.resourceSpecs().mapIndexed { index, spec ->
                val (path, size) = storageManager.createBundledPdfIfMissing(
                    fileName = spec.fileName,
                    title = spec.title,
                    category = spec.category,
                    sections = spec.sections,
                    apaReference = spec.apaReference
                )
                EducationalResourceEntity(
                    title = spec.title,
                    description = spec.description,
                    category = spec.category,
                    resourceType = spec.resourceType,
                    filePath = path,
                    mimeType = "application/pdf",
                    fileSizeBytes = size,
                    contentSummary = spec.sections.joinToString("\n\n") { "${it.first}\n${it.second}" } +
                        "\n\nReferencia (APA 7): ${spec.apaReference}",
                    publishedAtMillis = now - (index * 86_400_000L)
                )
            }
            db.resourceDao().insertAll(generatedResources)
        }
    }

    // --- Videos ---
    suspend fun ingestAndSaveVideo(
        title: String,
        description: String,
        category: String,
        subtitlesText: String,
        videoUri: Uri?,
        externalUrl: String,
        coverUri: Uri?,
        maxVideoMb: Int,
        maxPhotoMb: Int,
        onProgress: (Float) -> Unit
    ): Result<Unit> {
        var coverPath = ""
        if (coverUri != null) {
            when (val coverRes = storageManager.ingestImageFromUri(coverUri, isCover = true, maxSizeMb = maxPhotoMb) {}) {
                is MediaIngestionResult.Success -> coverPath = coverRes.filePath
                is MediaIngestionResult.Failure -> return Result.failure(IllegalStateException(coverRes.message))
            }
        }

        if (videoUri != null) {
            when (val res = storageManager.ingestVideoFromUri(videoUri, maxVideoMb, onProgress)) {
                is MediaIngestionResult.Success -> {
                    db.videoDao().insert(
                        VideoItemEntity(
                            title = title.trim(),
                            description = description.trim(),
                            category = category,
                            coverImagePath = coverPath,
                            videoUri = res.filePath,
                            isExternalUrl = false,
                            subtitlesText = subtitlesText.trim(),
                            durationMs = res.durationMs,
                            fileSizeBytes = res.fileSizeBytes
                        )
                    )
                    return Result.success(Unit)
                }
                is MediaIngestionResult.Failure -> {
                    storageManager.deleteFileIfManaged(coverPath)
                    return Result.failure(IllegalStateException(res.message))
                }
            }
        } else {
            val cleanedUrl = externalUrl.trim()
            if (!cleanedUrl.startsWith("http://") && !cleanedUrl.startsWith("https://")) {
                storageManager.deleteFileIfManaged(coverPath)
                return Result.failure(IllegalArgumentException("Ingresa un enlace de video válido (https://...) o selecciona un video del celular."))
            }
            onProgress(1.0f)
            db.videoDao().insert(
                VideoItemEntity(
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    coverImagePath = coverPath,
                    videoUri = cleanedUrl,
                    isExternalUrl = true,
                    subtitlesText = subtitlesText.trim()
                )
            )
            return Result.success(Unit)
        }
    }

    suspend fun updateVideoMetadata(video: VideoItemEntity) = db.videoDao().update(video)

    suspend fun deleteVideo(video: VideoItemEntity) {
        storageManager.deleteFileIfManaged(video.coverImagePath)
        if (!video.isExternalUrl) {
            storageManager.deleteFileIfManaged(video.videoUri)
        }
        db.videoDao().deleteById(video.id)
    }

    // --- Galleries & Photos ---
    suspend fun createGallery(title: String, description: String, category: String): Long {
        return db.galleryDao().insertGallery(
            PhotoGalleryEntity(title = title.trim(), description = description.trim(), category = category)
        )
    }

    suspend fun updateGallery(gallery: PhotoGalleryEntity) = db.galleryDao().updateGallery(gallery)

    suspend fun deleteGallery(gallery: PhotoGalleryEntity, galleryPhotos: List<PhotoItemEntity>) {
        galleryPhotos.forEach { storageManager.deleteFileIfManaged(it.imagePath) }
        db.galleryDao().deletePhotosByGalleryId(gallery.id)
        db.galleryDao().deleteGalleryById(gallery.id)
    }

    suspend fun ingestAndSavePhotos(
        galleryId: Int,
        baseTitle: String,
        description: String,
        category: String,
        uris: List<Uri>,
        maxPhotoMb: Int,
        onProgress: (Float) -> Unit
    ): Result<Int> {
        if (uris.isEmpty()) return Result.failure(IllegalArgumentException("Selecciona al menos una fotografía."))
        val savedPhotos = mutableListOf<PhotoItemEntity>()
        uris.forEachIndexed { idx, uri ->
            val res = storageManager.ingestImageFromUri(uri, isCover = false, maxSizeMb = maxPhotoMb) { fileProg ->
                val overall = (idx.toFloat() + fileProg) / uris.size.toFloat()
                onProgress(overall)
            }
            when (res) {
                is MediaIngestionResult.Success -> {
                    val photoTitle = if (uris.size == 1) baseTitle.trim() else "${baseTitle.trim()} (${idx + 1})"
                    savedPhotos.add(
                        PhotoItemEntity(
                            galleryId = galleryId,
                            title = photoTitle,
                            description = description.trim(),
                            category = category,
                            imagePath = res.filePath,
                            fileSizeBytes = res.fileSizeBytes
                        )
                    )
                }
                is MediaIngestionResult.Failure -> {
                    savedPhotos.forEach { storageManager.deleteFileIfManaged(it.imagePath) }
                    return Result.failure(IllegalStateException(res.message))
                }
            }
        }
        db.galleryDao().insertPhotos(savedPhotos)
        onProgress(1.0f)
        return Result.success(savedPhotos.size)
    }

    suspend fun updatePhoto(photo: PhotoItemEntity) = db.galleryDao().updatePhoto(photo)

    suspend fun deletePhoto(photo: PhotoItemEntity) {
        storageManager.deleteFileIfManaged(photo.imagePath)
        db.galleryDao().deletePhotoById(photo.id)
    }

    // --- Articles ---
    suspend fun saveArticle(
        existingId: Int?,
        title: String,
        category: String,
        introduction: String,
        bodyContent: String,
        conclusions: String,
        apaReferences: String,
        isFeatured: Boolean,
        coverUri: Uri?,
        existingCoverPath: String,
        maxPhotoMb: Int
    ): Result<Unit> {
        var coverPath = existingCoverPath
        if (coverUri != null) {
            when (val res = storageManager.ingestImageFromUri(coverUri, isCover = true, maxSizeMb = maxPhotoMb) {}) {
                is MediaIngestionResult.Success -> {
                    if (existingCoverPath.isNotBlank()) storageManager.deleteFileIfManaged(existingCoverPath)
                    coverPath = res.filePath
                }
                is MediaIngestionResult.Failure -> return Result.failure(IllegalStateException(res.message))
            }
        }
        val article = ArticleEntity(
            id = existingId ?: 0,
            title = title.trim(),
            category = category,
            coverImagePath = coverPath,
            bundledIllustrationKey = if (coverPath.isBlank()) "brain" else "",
            introduction = introduction.trim(),
            bodyContent = bodyContent.trim(),
            conclusions = conclusions.trim(),
            apaReferences = apaReferences.trim(),
            isFeatured = isFeatured
        )
        if (existingId != null && existingId > 0) {
            db.articleDao().update(article)
        } else {
            db.articleDao().insert(article)
        }
        return Result.success(Unit)
    }

    suspend fun deleteArticle(article: ArticleEntity) {
        storageManager.deleteFileIfManaged(article.coverImagePath)
        db.articleDao().deleteById(article.id)
    }

    // --- Educational Resources ---
    suspend fun ingestAndSaveResource(
        title: String,
        description: String,
        category: String,
        resourceType: String,
        documentUri: Uri,
        maxDocMb: Int,
        onProgress: (Float) -> Unit
    ): Result<Unit> {
        return when (val res = storageManager.ingestDocumentFromUri(documentUri, maxDocMb, onProgress)) {
            is MediaIngestionResult.Success -> {
                db.resourceDao().insert(
                    EducationalResourceEntity(
                        title = title.trim(),
                        description = description.trim(),
                        category = category,
                        resourceType = resourceType,
                        filePath = res.filePath,
                        mimeType = res.mimeType,
                        fileSizeBytes = res.fileSizeBytes,
                        contentSummary = description.trim()
                    )
                )
                Result.success(Unit)
            }
            is MediaIngestionResult.Failure -> Result.failure(IllegalStateException(res.message))
        }
    }

    suspend fun deleteResource(resource: EducationalResourceEntity) {
        storageManager.deleteFileIfManaged(resource.filePath)
        db.resourceDao().deleteById(resource.id)
    }

    // --- Categories ---
    suspend fun addCategory(name: String, scope: String) {
        if (name.isBlank()) return
        db.categoryDao().insert(CategoryEntity(name = name.trim(), scope = scope, isSystemDefault = false))
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        db.categoryDao().deleteById(category.id)
    }

    // --- Contact ---
    suspend fun submitContactMessage(name: String, email: String, subject: String, message: String) {
        db.contactMessageDao().insert(
            ContactMessageEntity(
                senderName = name.trim(),
                senderEmail = email.trim(),
                subject = subject.trim(),
                message = message.trim()
            )
        )
    }

    suspend fun markContactRead(id: Int) = db.contactMessageDao().markRead(id)
    suspend fun deleteContactMessage(id: Int) = db.contactMessageDao().deleteById(id)

    suspend fun getStorageUsage(): StorageUsageSummary = storageManager.getStorageUsage()
}
