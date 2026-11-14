package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ArticleEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.EducationalResourceEntity
import com.example.data.model.MentalDisorderEntity
import com.example.data.model.PhotoGalleryEntity
import com.example.data.model.PhotoItemEntity
import com.example.data.model.VideoItemEntity
import com.example.data.preferences.AccessibilitySettings
import com.example.data.preferences.AdminAuthState
import com.example.data.preferences.AppPreferencesRepository
import com.example.data.preferences.StorageLimitsConfig
import com.example.data.repository.PsychologyRepository
import com.example.data.storage.StorageUsageSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UploadTaskState(
    val isUploading: Boolean = false,
    val progress: Float = 0f,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
    val suggestExternalVideoUrl: Boolean = false
)

data class AdminSessionState(
    val isAuthenticated: Boolean = false,
    val authError: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = PsychologyRepository(application)
    private val preferences = AppPreferencesRepository(application)

    val disorders: StateFlow<List<MentalDisorderEntity>> = repository.disorders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val videos: StateFlow<List<VideoItemEntity>> = repository.videos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val galleries: StateFlow<List<PhotoGalleryEntity>> = repository.galleries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val photos: StateFlow<List<PhotoItemEntity>> = repository.photos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val articles: StateFlow<List<ArticleEntity>> = repository.articles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val resources: StateFlow<List<EducationalResourceEntity>> = repository.resources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contactMessages: StateFlow<List<ContactMessageEntity>> = repository.contactMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accessibilitySettings: StateFlow<AccessibilitySettings> = preferences.accessibilitySettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccessibilitySettings())

    val storageLimits: StateFlow<StorageLimitsConfig> = preferences.storageLimits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StorageLimitsConfig())

    val adminAuthState: StateFlow<AdminAuthState> = preferences.adminAuthState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminAuthState())

    private val _adminSession = MutableStateFlow(AdminSessionState())
    val adminSession: StateFlow<AdminSessionState> = _adminSession.asStateFlow()

    private val _uploadState = MutableStateFlow(UploadTaskState())
    val uploadState: StateFlow<UploadTaskState> = _uploadState.asStateFlow()

    private val _storageUsage = MutableStateFlow(
        StorageUsageSummary(0L, 0L, 0L, 0L, 0L)
    )
    val storageUsage: StateFlow<StorageUsageSummary> = _storageUsage.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            refreshStorageUsage()
            _isInitialized.value = true
        }
    }

    fun refreshStorageUsage() {
        viewModelScope.launch {
            _storageUsage.value = repository.getStorageUsage()
        }
    }

    // --- Accessibility ---
    fun updateFontScale(scale: Float) {
        viewModelScope.launch { preferences.setFontScale(scale) }
    }

    fun updateHighContrast(enabled: Boolean) {
        viewModelScope.launch { preferences.setHighContrast(enabled) }
    }

    fun updateDarkThemeMode(mode: String) {
        viewModelScope.launch { preferences.setDarkThemeMode(mode) }
    }

    // --- Admin Authentication ---
    fun setupAdminCredentials(displayName: String, password: String) {
        if (password.length < 6) {
            _adminSession.update { it.copy(authError = "La clave debe tener mínimo 6 caracteres.") }
            return
        }
        viewModelScope.launch {
            preferences.configureAdminAccess(displayName, password)
            _adminSession.value = AdminSessionState(isAuthenticated = true, authError = null)
        }
    }

    fun loginAdmin(password: String) {
        viewModelScope.launch {
            val ok = preferences.verifyAdminPassword(password)
            if (ok) {
                _adminSession.value = AdminSessionState(isAuthenticated = true, authError = null)
            } else {
                _adminSession.update { it.copy(authError = "Clave incorrecta. Intenta nuevamente.") }
            }
        }
    }

    fun logoutAdmin() {
        _adminSession.value = AdminSessionState(isAuthenticated = false, authError = null)
    }

    fun clearAuthError() {
        _adminSession.update { it.copy(authError = null) }
    }

    // --- Storage Config ---
    fun updateStorageLimits(videoMb: Int, photoMb: Int, docMb: Int) {
        viewModelScope.launch {
            preferences.updateStorageLimits(videoMb, photoMb, docMb)
            _uploadState.update { it.copy(statusMessage = "Límites de carga actualizados correctamente.") }
        }
    }

    fun clearUploadFeedback() {
        _uploadState.value = UploadTaskState()
    }

    // --- Video Upload & CRUD ---
    fun uploadVideo(
        title: String,
        description: String,
        category: String,
        subtitlesText: String,
        videoUri: Uri?,
        externalUrl: String,
        coverUri: Uri?,
        onSuccess: () -> Unit
    ) {
        if (title.isBlank()) {
            _uploadState.value = UploadTaskState(errorMessage = "El título del video es obligatorio.")
            return
        }
        if (videoUri == null && externalUrl.isBlank()) {
            _uploadState.value = UploadTaskState(errorMessage = "Selecciona un video desde tu celular o ingresa un enlace externo.")
            return
        }
        viewModelScope.launch {
            val limits = storageLimits.value
            _uploadState.value = UploadTaskState(isUploading = true, progress = 0.05f)
            val result = repository.ingestAndSaveVideo(
                title = title,
                description = description,
                category = category,
                subtitlesText = subtitlesText,
                videoUri = videoUri,
                externalUrl = externalUrl,
                coverUri = coverUri,
                maxVideoMb = limits.maxVideoMb,
                maxPhotoMb = limits.maxPhotoMb,
                onProgress = { p ->
                    _uploadState.update { it.copy(progress = p) }
                }
            )
            result.fold(
                onSuccess = {
                    refreshStorageUsage()
                    _uploadState.value = UploadTaskState(
                        isUploading = false,
                        progress = 1f,
                        statusMessage = "Video publicado correctamente en la biblioteca audiovisual."
                    )
                    onSuccess()
                },
                onFailure = { err ->
                    val msg = err.message ?: "Error al publicar el video."
                    _uploadState.value = UploadTaskState(
                        isUploading = false,
                        errorMessage = msg,
                        suggestExternalVideoUrl = msg.contains("límite", ignoreCase = true) ||
                            msg.contains("espacio", ignoreCase = true)
                    )
                }
            )
        }
    }

    fun updateVideo(video: VideoItemEntity) {
        viewModelScope.launch {
            repository.updateVideoMetadata(video)
            _uploadState.value = UploadTaskState(statusMessage = "Video actualizado correctamente.")
        }
    }

    fun deleteVideo(video: VideoItemEntity) {
        viewModelScope.launch {
            repository.deleteVideo(video)
            refreshStorageUsage()
            _uploadState.value = UploadTaskState(statusMessage = "Video eliminado.")
        }
    }

    // --- Galleries & Photos ---
    fun createGallery(title: String, description: String, category: String, onCreated: (Int) -> Unit = {}) {
        if (title.isBlank()) {
            _uploadState.value = UploadTaskState(errorMessage = "El título de la galería es obligatorio.")
            return
        }
        viewModelScope.launch {
            val id = repository.createGallery(title, description, category).toInt()
            _uploadState.value = UploadTaskState(statusMessage = "Galería \"$title\" creada correctamente.")
            onCreated(id)
        }
    }

    fun deleteGallery(gallery: PhotoGalleryEntity) {
        viewModelScope.launch {
            val galleryPhotos = photos.value.filter { it.galleryId == gallery.id }
            repository.deleteGallery(gallery, galleryPhotos)
            refreshStorageUsage()
            _uploadState.value = UploadTaskState(statusMessage = "Galería eliminada.")
        }
    }

    fun uploadPhotos(
        galleryId: Int,
        baseTitle: String,
        description: String,
        category: String,
        uris: List<Uri>,
        onSuccess: () -> Unit
    ) {
        if (baseTitle.isBlank()) {
            _uploadState.value = UploadTaskState(errorMessage = "Ingresa un título para las fotografías.")
            return
        }
        if (uris.isEmpty()) {
            _uploadState.value = UploadTaskState(errorMessage = "Selecciona al menos una fotografía de tu galería.")
            return
        }
        viewModelScope.launch {
            val limits = storageLimits.value
            _uploadState.value = UploadTaskState(isUploading = true, progress = 0.05f)
            val result = repository.ingestAndSavePhotos(
                galleryId = galleryId,
                baseTitle = baseTitle,
                description = description,
                category = category,
                uris = uris,
                maxPhotoMb = limits.maxPhotoMb,
                onProgress = { p -> _uploadState.update { it.copy(progress = p) } }
            )
            result.fold(
                onSuccess = { count ->
                    refreshStorageUsage()
                    _uploadState.value = UploadTaskState(
                        isUploading = false,
                        progress = 1f,
                        statusMessage = "$count fotografía(s) publicadas correctamente."
                    )
                    onSuccess()
                },
                onFailure = { err ->
                    _uploadState.value = UploadTaskState(
                        isUploading = false,
                        errorMessage = err.message ?: "Error al subir las fotografías."
                    )
                }
            )
        }
    }

    fun updatePhoto(photo: PhotoItemEntity) {
        viewModelScope.launch {
            repository.updatePhoto(photo)
            _uploadState.value = UploadTaskState(statusMessage = "Fotografía actualizada.")
        }
    }

    fun deletePhoto(photo: PhotoItemEntity) {
        viewModelScope.launch {
            repository.deletePhoto(photo)
            refreshStorageUsage()
            _uploadState.value = UploadTaskState(statusMessage = "Fotografía eliminada.")
        }
    }

    // --- Articles ---
    fun saveArticle(
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
        onSuccess: () -> Unit
    ) {
        if (title.isBlank() || introduction.isBlank() || bodyContent.isBlank()) {
            _uploadState.value = UploadTaskState(errorMessage = "Completa título, introducción y desarrollo del artículo.")
            return
        }
        viewModelScope.launch {
            val res = repository.saveArticle(
                existingId = existingId,
                title = title,
                category = category,
                introduction = introduction,
                bodyContent = bodyContent,
                conclusions = conclusions,
                apaReferences = apaReferences,
                isFeatured = isFeatured,
                coverUri = coverUri,
                existingCoverPath = existingCoverPath,
                maxPhotoMb = storageLimits.value.maxPhotoMb
            )
            res.fold(
                onSuccess = {
                    refreshStorageUsage()
                    _uploadState.value = UploadTaskState(statusMessage = "Artículo publicado correctamente.")
                    onSuccess()
                },
                onFailure = { err ->
                    _uploadState.value = UploadTaskState(errorMessage = err.message ?: "No se pudo guardar el artículo.")
                }
            )
        }
    }

    fun deleteArticle(article: ArticleEntity) {
        viewModelScope.launch {
            repository.deleteArticle(article)
            refreshStorageUsage()
            _uploadState.value = UploadTaskState(statusMessage = "Artículo eliminado.")
        }
    }

    // --- Educational Resources ---
    fun uploadResourceDocument(
        title: String,
        description: String,
        category: String,
        resourceType: String,
        documentUri: Uri?,
        onSuccess: () -> Unit
    ) {
        if (title.isBlank() || documentUri == null) {
            _uploadState.value = UploadTaskState(errorMessage = "Ingresa un título y selecciona un archivo PDF o imagen.")
            return
        }
        viewModelScope.launch {
            _uploadState.value = UploadTaskState(isUploading = true, progress = 0.1f)
            val res = repository.ingestAndSaveResource(
                title = title,
                description = description,
                category = category,
                resourceType = resourceType,
                documentUri = documentUri,
                maxDocMb = storageLimits.value.maxDocumentMb,
                onProgress = { p -> _uploadState.update { it.copy(progress = p) } }
            )
            res.fold(
                onSuccess = {
                    refreshStorageUsage()
                    _uploadState.value = UploadTaskState(
                        isUploading = false,
                        progress = 1f,
                        statusMessage = "Recurso educativo publicado correctamente."
                    )
                    onSuccess()
                },
                onFailure = { err ->
                    _uploadState.value = UploadTaskState(
                        isUploading = false,
                        errorMessage = err.message ?: "No se pudo subir el documento."
                    )
                }
            )
        }
    }

    fun deleteResource(resource: EducationalResourceEntity) {
        viewModelScope.launch {
            repository.deleteResource(resource)
            refreshStorageUsage()
            _uploadState.value = UploadTaskState(statusMessage = "Recurso educativo eliminado.")
        }
    }

    // --- Categories ---
    fun addCategory(name: String, scope: String) {
        viewModelScope.launch {
            repository.addCategory(name, scope)
            _uploadState.value = UploadTaskState(statusMessage = "Categoría agregada.")
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            _uploadState.value = UploadTaskState(statusMessage = "Categoría eliminada.")
        }
    }

    // --- Contact ---
    fun submitContactMessage(name: String, email: String, subject: String, message: String) {
        viewModelScope.launch {
            repository.submitContactMessage(name, email, subject, message)
        }
    }

    fun markContactRead(id: Int) {
        viewModelScope.launch { repository.markContactRead(id) }
    }

    fun deleteContactMessage(id: Int) {
        viewModelScope.launch { repository.deleteContactMessage(id) }
    }
}
