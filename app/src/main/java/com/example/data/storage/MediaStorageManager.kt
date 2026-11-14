package com.example.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

sealed class MediaIngestionResult {
    data class Success(
        val filePath: String,
        val fileSizeBytes: Long,
        val durationMs: Long = 0L,
        val mimeType: String
    ) : MediaIngestionResult()

    data class Failure(val message: String, val exceedsSizeLimit: Boolean = false) : MediaIngestionResult()
}

data class StorageUsageSummary(
    val videosBytes: Long,
    val photosBytes: Long,
    val coversBytes: Long,
    val docsBytes: Long,
    val availableDeviceBytes: Long
) {
    val totalUsedBytes: Long get() = videosBytes + photosBytes + coversBytes + docsBytes
}

class MediaStorageManager(private val context: Context) {

    private val mediaRoot: File get() = File(context.filesDir, "media").apply { mkdirs() }
    private val videosDir: File get() = File(mediaRoot, "videos").apply { mkdirs() }
    private val coversDir: File get() = File(mediaRoot, "covers").apply { mkdirs() }
    private val photosDir: File get() = File(mediaRoot, "photos").apply { mkdirs() }
    private val docsDir: File get() = File(mediaRoot, "docs").apply { mkdirs() }

    private val allowedVideoMimes = setOf(
        "video/mp4", "video/webm", "video/3gpp", "video/x-matroska", "video/quicktime"
    )
    private val allowedImageMimes = setOf(
        "image/jpeg", "image/jpg", "image/png", "image/webp", "image/heic", "image/heif"
    )
    private val allowedDocMimes = setOf(
        "application/pdf", "image/png", "image/jpeg", "text/plain"
    )

    suspend fun ingestVideoFromUri(
        uri: Uri,
        maxSizeMb: Int,
        onProgress: (Float) -> Unit
    ): MediaIngestionResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val mime = resolver.getType(uri)?.lowercase() ?: guessMimeFromName(uri)
        if (mime !in allowedVideoMimes && !mime.startsWith("video/")) {
            return@withContext MediaIngestionResult.Failure(
                "Formato de video no compatible ($mime). Usa MP4, WebM, MOV o MKV."
            )
        }

        val reportedSize = queryFileSize(uri)
        val maxBytes = maxSizeMb.toLong() * 1024L * 1024L
        if (reportedSize > 0L && reportedSize > maxBytes) {
            return@withContext MediaIngestionResult.Failure(
                "El video pesa ${formatSize(reportedSize)} y supera el límite configurado de $maxSizeMb MB. Puedes aumentar el límite o usar un enlace externo de video.",
                exceedsSizeLimit = true
            )
        }
        if (reportedSize > 0L && reportedSize > mediaRoot.usableSpace - 20_000_000L) {
            return@withContext MediaIngestionResult.Failure(
                "Espacio insuficiente en el almacenamiento del dispositivo. Usa un enlace externo de video como alternativa.",
                exceedsSizeLimit = true
            )
        }

        val ext = when {
            mime.contains("webm") -> "webm"
            mime.contains("matroska") -> "mkv"
            mime.contains("quicktime") -> "mov"
            else -> "mp4"
        }
        val destFile = File(videosDir, "vid_${UUID.randomUUID()}.$ext")
        try {
            var copied = 0L
            resolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    val buffer = ByteArray(64 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read <= 0) break
                        copied += read
                        if (copied > maxBytes) {
                            destFile.delete()
                            return@withContext MediaIngestionResult.Failure(
                                "El archivo superó el límite de $maxSizeMb MB durante la carga. Puedes usar un enlace externo.",
                                exceedsSizeLimit = true
                            )
                        }
                        output.write(buffer, 0, read)
                        if (reportedSize > 0) {
                            onProgress((copied.toFloat() / reportedSize.toFloat()).coerceIn(0f, 0.95f))
                        } else {
                            onProgress(0.5f)
                        }
                    }
                }
            } ?: return@withContext MediaIngestionResult.Failure("No se pudo abrir el archivo de video seleccionado.")

            val durationMs = extractVideoDuration(destFile.absolutePath)
            onProgress(1.0f)
            MediaIngestionResult.Success(
                filePath = destFile.absolutePath,
                fileSizeBytes = destFile.length(),
                durationMs = durationMs,
                mimeType = mime
            )
        } catch (e: Exception) {
            destFile.delete()
            MediaIngestionResult.Failure("Error al guardar el video: ${e.localizedMessage ?: "error desconocido"}")
        }
    }

    suspend fun ingestImageFromUri(
        uri: Uri,
        isCover: Boolean,
        maxSizeMb: Int,
        onProgress: (Float) -> Unit
    ): MediaIngestionResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val mime = resolver.getType(uri)?.lowercase() ?: "image/jpeg"
        if (mime !in allowedImageMimes && !mime.startsWith("image/")) {
            return@withContext MediaIngestionResult.Failure(
                "Formato de imagen no válido ($mime). Usa JPG, PNG o WebP."
            )
        }

        val reportedSize = queryFileSize(uri)
        val maxBytes = maxSizeMb.toLong() * 1024L * 1024L
        if (reportedSize > 0L && reportedSize > maxBytes) {
            return@withContext MediaIngestionResult.Failure(
                "La imagen (${formatSize(reportedSize)}) supera el límite de $maxSizeMb MB.",
                exceedsSizeLimit = true
            )
        }

        val targetDir = if (isCover) coversDir else photosDir
        val destFile = File(targetDir, "img_${UUID.randomUUID()}.jpg")
        try {
            onProgress(0.25f)
            val bitmap = decodeSampledBitmap(uri, maxDim = if (isCover) 1280 else 1600)
                ?: return@withContext MediaIngestionResult.Failure("No se pudo decodificar la imagen seleccionada.")
            onProgress(0.65f)
            FileOutputStream(destFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
            }
            bitmap.recycle()
            onProgress(1.0f)
            MediaIngestionResult.Success(
                filePath = destFile.absolutePath,
                fileSizeBytes = destFile.length(),
                mimeType = "image/jpeg"
            )
        } catch (e: Exception) {
            destFile.delete()
            MediaIngestionResult.Failure("Error al procesar la imagen: ${e.localizedMessage ?: "error desconocido"}")
        }
    }

    suspend fun ingestDocumentFromUri(
        uri: Uri,
        maxSizeMb: Int,
        onProgress: (Float) -> Unit
    ): MediaIngestionResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val mime = resolver.getType(uri)?.lowercase() ?: guessMimeFromName(uri)
        if (mime !in allowedDocMimes && !mime.contains("pdf") && !mime.startsWith("image/")) {
            return@withContext MediaIngestionResult.Failure(
                "Formato no compatible ($mime). Selecciona un PDF, PNG o JPG."
            )
        }

        val reportedSize = queryFileSize(uri)
        val maxBytes = maxSizeMb.toLong() * 1024L * 1024L
        if (reportedSize > 0L && reportedSize > maxBytes) {
            return@withContext MediaIngestionResult.Failure(
                "El documento (${formatSize(reportedSize)}) supera el límite de $maxSizeMb MB.",
                exceedsSizeLimit = true
            )
        }

        val ext = when {
            mime.contains("pdf") -> "pdf"
            mime.contains("png") -> "png"
            mime.contains("jpeg") || mime.contains("jpg") -> "jpg"
            else -> "pdf"
        }
        val destFile = File(docsDir, "doc_${UUID.randomUUID()}.$ext")
        try {
            var copied = 0L
            resolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    val buffer = ByteArray(32 * 1024)
                    while (true) {
                        val read = input.read(buffer)
                        if (read <= 0) break
                        copied += read
                        if (copied > maxBytes) {
                            destFile.delete()
                            return@withContext MediaIngestionResult.Failure(
                                "El documento superó el límite de $maxSizeMb MB.",
                                exceedsSizeLimit = true
                            )
                        }
                        output.write(buffer, 0, read)
                        if (reportedSize > 0) {
                            onProgress((copied.toFloat() / reportedSize.toFloat()).coerceIn(0f, 0.95f))
                        } else {
                            onProgress(0.6f)
                        }
                    }
                }
            } ?: return@withContext MediaIngestionResult.Failure("No se pudo leer el archivo seleccionado.")
            onProgress(1.0f)
            MediaIngestionResult.Success(
                filePath = destFile.absolutePath,
                fileSizeBytes = destFile.length(),
                mimeType = mime
            )
        } catch (e: Exception) {
            destFile.delete()
            MediaIngestionResult.Failure("Error al guardar el documento: ${e.localizedMessage ?: "error"}")
        }
    }

    suspend fun createBundledPdfIfMissing(
        fileName: String,
        title: String,
        category: String,
        sections: List<Pair<String, String>>,
        apaReference: String
    ): Pair<String, Long> = withContext(Dispatchers.IO) {
        val file = File(docsDir, fileName)
        if (file.exists() && file.length() > 0L) {
            return@withContext file.absolutePath to file.length()
        }

        val doc = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 48
        val contentWidth = pageWidth - (margin * 2)

        val headerPaint = Paint().apply {
            color = Color.rgb(19, 49, 92)
            style = Paint.Style.FILL
        }
        val titlePaint = TextPaint().apply {
            color = Color.WHITE
            textSize = 20f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        val subtitlePaint = TextPaint().apply {
            color = Color.rgb(214, 230, 242)
            textSize = 11f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        val sectionTitlePaint = TextPaint().apply {
            color = Color.rgb(19, 49, 92)
            textSize = 14f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            isAntiAlias = true
        }
        val bodyPaint = TextPaint().apply {
            color = Color.rgb(28, 42, 58)
            textSize = 11.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            isAntiAlias = true
        }
        val footerPaint = TextPaint().apply {
            color = Color.rgb(79, 115, 96)
            textSize = 9.5f
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
            isAntiAlias = true
        }

        var pageNumber = 1
        var page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas

        // Header banner
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 112f, headerPaint)
        canvas.drawText("PSICOLOGÍA PARA TODOS · $category".uppercase(), margin.toFloat(), 36f, subtitlePaint)
        drawWrappedText(canvas, title, titlePaint, margin.toFloat(), 52f, contentWidth)

        var cursorY = 140f

        for ((secHeading, secBody) in sections) {
            val headingLayout = StaticLayout.Builder.obtain(secHeading, 0, secHeading.length, sectionTitlePaint, contentWidth).build()
            val bodyLayout = StaticLayout.Builder.obtain(secBody, 0, secBody.length, bodyPaint, contentWidth)
                .setLineSpacing(4f, 1.15f)
                .build()

            if (cursorY + headingLayout.height + bodyLayout.height + 60f > pageHeight - 60f) {
                drawPdfFooter(canvas, pageWidth, pageHeight, margin, footerPaint, pageNumber)
                doc.finishPage(page)
                pageNumber++
                page = doc.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
                canvas = page.canvas
                cursorY = 56f
            }

            canvas.save()
            canvas.translate(margin.toFloat(), cursorY)
            headingLayout.draw(canvas)
            canvas.restore()
            cursorY += headingLayout.height + 8f

            canvas.save()
            canvas.translate(margin.toFloat(), cursorY)
            bodyLayout.draw(canvas)
            canvas.restore()
            cursorY += bodyLayout.height + 18f
        }

        val refLayout = StaticLayout.Builder.obtain("Referencia (APA 7): $apaReference", 0, ("Referencia (APA 7): $apaReference").length, footerPaint, contentWidth).build()
        if (cursorY + refLayout.height + 40f < pageHeight - 50f) {
            canvas.save()
            canvas.translate(margin.toFloat(), cursorY + 8f)
            refLayout.draw(canvas)
            canvas.restore()
        }
        drawPdfFooter(canvas, pageWidth, pageHeight, margin, footerPaint, pageNumber)
        doc.finishPage(page)

        FileOutputStream(file).use { out -> doc.writeTo(out) }
        doc.close()
        file.absolutePath to file.length()
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        paint: TextPaint,
        x: Float,
        y: Float,
        width: Int
    ) {
        val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width).build()
        canvas.save()
        canvas.translate(x, y)
        layout.draw(canvas)
        canvas.restore()
    }

    private fun drawPdfFooter(
        canvas: Canvas,
        pageWidth: Int,
        pageHeight: Int,
        margin: Int,
        paint: TextPaint,
        pageNum: Int
    ) {
        val disclaimer = "Material psicoeducativo de Psicología para Todos. No sustituye atención psicológica profesional. Pág. $pageNum"
        canvas.drawText(disclaimer, margin.toFloat(), (pageHeight - 28).toFloat(), paint)
    }

    suspend fun renderPdfFirstPagePreview(filePath: String): Bitmap? = withContext(Dispatchers.IO) {
        val file = File(filePath)
        if (!file.exists()) return@withContext null
        try {
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    if (renderer.pageCount <= 0) return@withContext null
                    renderer.openPage(0).use { page ->
                        val width = 720
                        val height = (width.toFloat() * page.height / page.width).toInt().coerceAtLeast(400)
                        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        bitmap.eraseColor(Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    }
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    fun getShareableUri(filePath: String): Uri? {
        val file = File(filePath)
        if (!file.exists()) return null
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun deleteFileIfManaged(filePath: String) {
        if (filePath.isBlank()) return
        val file = File(filePath)
        if (file.exists() && file.absolutePath.startsWith(mediaRoot.absolutePath)) {
            file.delete()
        }
    }

    suspend fun getStorageUsage(): StorageUsageSummary = withContext(Dispatchers.IO) {
        StorageUsageSummary(
            videosBytes = dirSize(videosDir),
            photosBytes = dirSize(photosDir),
            coversBytes = dirSize(coversDir),
            docsBytes = dirSize(docsDir),
            availableDeviceBytes = context.filesDir.usableSpace
        )
    }

    private fun dirSize(dir: File): Long {
        if (!dir.exists()) return 0L
        return dir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    }

    private fun decodeSampledBitmap(uri: Uri, maxDim: Int): Bitmap? {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sampleSize = 1
        while (bounds.outWidth / sampleSize > maxDim || bounds.outHeight / sampleSize > maxDim) {
            sampleSize *= 2
        }
        val opts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
    }

    private fun extractVideoDuration(path: String): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(path)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        } catch (_: Exception) {
            0L
        } finally {
            runCatching { retriever.release() }
        }
    }

    private fun queryFileSize(uri: Uri): Long {
        return try {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { c ->
                val idx = c.getColumnIndex(OpenableColumns.SIZE)
                if (idx >= 0 && c.moveToFirst()) c.getLong(idx) else -1L
            } ?: -1L
        } catch (_: Exception) {
            -1L
        }
    }

    private fun guessMimeFromName(uri: Uri): String {
        val lower = uri.toString().lowercase()
        return when {
            lower.endsWith(".mp4") -> "video/mp4"
            lower.endsWith(".webm") -> "video/webm"
            lower.endsWith(".mkv") -> "video/x-matroska"
            lower.endsWith(".mov") -> "video/quicktime"
            lower.endsWith(".pdf") -> "application/pdf"
            lower.endsWith(".png") -> "image/png"
            else -> "application/octet-stream"
        }
    }

    companion object {
        fun formatSize(bytes: Long): String {
            if (bytes <= 0L) return "0 KB"
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            return if (mb >= 1.0) String.format("%.1f MB", mb) else String.format("%.0f KB", kb)
        }

        fun formatDuration(ms: Long): String {
            if (ms <= 0L) return "Video"
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}
