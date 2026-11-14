package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.ArticleEntity
import com.example.data.model.PhotoGalleryEntity
import com.example.data.model.PhotoItemEntity
import com.example.data.model.VideoItemEntity
import com.example.ui.viewmodel.UploadTaskState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdownField(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun UploadVideoDialog(
    categories: List<String>,
    maxVideoMb: Int,
    uploadState: UploadTaskState,
    onDismiss: () -> Unit,
    onPublish: (
        title: String,
        description: String,
        category: String,
        subtitles: String,
        videoUri: Uri?,
        externalUrl: String,
        coverUri: Uri?
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.firstOrNull() ?: "Inteligencia emocional") }
    var subtitles by remember { mutableStateOf("") }
    var pickedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var pickedCoverUri by remember { mutableStateOf<Uri?>(null) }
    var externalUrl by remember { mutableStateOf("") }
    var useExternalLink by remember { mutableStateOf(false) }

    val videoGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedVideoUri = uri
            useExternalLink = false
        }
    }

    val videoFileManagerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pickedVideoUri = uri
            useExternalLink = false
        }
    }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) pickedCoverUri = uri
    }

    AlertDialog(
        onDismissRequest = { if (!uploadState.isUploading) onDismiss() },
        title = { Text("Subir Video desde Android", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Formatos compatibles: MP4, WebM, MOV, MKV · Límite actual: $maxVideoMb MB.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = {
                            videoGalleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        enabled = !uploadState.isUploading,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("pick_video_gallery_btn")
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Galería")
                    }
                    OutlinedButton(
                        onClick = {
                            videoFileManagerLauncher.launch(arrayOf("video/mp4", "video/*"))
                        },
                        enabled = !uploadState.isUploading,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("pick_video_files_btn")
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Archivos")
                    }
                }

                if (pickedVideoUri != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Video seleccionado: ${pickedVideoUri?.lastPathSegment ?: "archivo del teléfono"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = useExternalLink || uploadState.suggestExternalVideoUrl,
                        onCheckedChange = { useExternalLink = it }
                    )
                    Text("Usar enlace externo de video (alternativa por límite)", style = MaterialTheme.typography.bodySmall)
                }

                if (useExternalLink || uploadState.suggestExternalVideoUrl) {
                    OutlinedTextField(
                        value = externalUrl,
                        onValueChange = { externalUrl = it },
                        label = { Text("URL externa (https://...mp4)") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del video") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("video_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                CategoryDropdownField(
                    label = "Categoría",
                    selected = category,
                    options = categories.ifEmpty { listOf("Inteligencia emocional") },
                    onSelected = { category = it }
                )

                OutlinedButton(
                    onClick = {
                        coverPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    enabled = !uploadState.isUploading,
                    modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (pickedCoverUri == null) "Agregar imagen de portada (opcional)" else "Portada seleccionada")
                }

                OutlinedTextField(
                    value = subtitles,
                    onValueChange = { subtitles = it },
                    label = { Text("Subtítulos / transcripción (opcional)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uploadState.isUploading) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LinearProgressIndicator(
                            progress = { uploadState.progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Procesando y guardando video... ${(uploadState.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                if (!uploadState.errorMessage.isNullOrBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = uploadState.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPublish(
                        title,
                        description,
                        category,
                        subtitles,
                        if (useExternalLink) null else pickedVideoUri,
                        if (useExternalLink || uploadState.suggestExternalVideoUrl) externalUrl else "",
                        pickedCoverUri
                    )
                },
                enabled = !uploadState.isUploading,
                modifier = Modifier.minimumInteractiveComponentSize().testTag("publish_video_confirm_btn")
            ) {
                Text("Publicar video")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !uploadState.isUploading,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun UploadPhotosDialog(
    galleries: List<PhotoGalleryEntity>,
    categories: List<String>,
    maxPhotoMb: Int,
    uploadState: UploadTaskState,
    onDismiss: () -> Unit,
    onQuickCreateGallery: (title: String, category: String) -> Unit,
    onPublish: (
        galleryId: Int,
        baseTitle: String,
        description: String,
        category: String,
        uris: List<Uri>
    ) -> Unit
) {
    var selectedGalleryId by remember(galleries) { mutableStateOf(galleries.firstOrNull()?.id ?: 0) }
    var baseTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.firstOrNull() ?: "Jornadas psicoeducativas") }
    var pickedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var quickGalleryTitle by remember { mutableStateOf("") }

    val multiPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(20)
    ) { uris ->
        if (uris.isNotEmpty()) pickedUris = uris
    }

    val fileManagerPhotosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) pickedUris = uris
    }

    AlertDialog(
        onDismissRequest = { if (!uploadState.isUploading) onDismiss() },
        title = { Text("Subir Fotografías desde Android", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Selecciona una o varias fotografías (JPG, PNG, WebP · Máx $maxPhotoMb MB c/u).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = {
                            multiPhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        enabled = !uploadState.isUploading,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("pick_photos_gallery_btn")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Galería")
                    }
                    OutlinedButton(
                        onClick = { fileManagerPhotosLauncher.launch(arrayOf("image/*")) },
                        enabled = !uploadState.isUploading,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Archivos")
                    }
                }

                if (pickedUris.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "${pickedUris.size} fotografía(s) seleccionada(s)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (galleries.isEmpty()) {
                    OutlinedTextField(
                        value = quickGalleryTitle,
                        onValueChange = { quickGalleryTitle = it },
                        label = { Text("Nombre para nueva galería") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedButton(
                        onClick = {
                            if (quickGalleryTitle.isNotBlank()) {
                                onQuickCreateGallery(quickGalleryTitle, category)
                                quickGalleryTitle = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Crear galería primero")
                    }
                } else {
                    val selectedName = galleries.find { it.id == selectedGalleryId }?.title ?: galleries.first().title
                    CategoryDropdownField(
                        label = "Galería destino",
                        selected = selectedName,
                        options = galleries.map { it.title },
                        onSelected = { chosen ->
                            selectedGalleryId = galleries.firstOrNull { it.title == chosen }?.id ?: 0
                        }
                    )
                }

                OutlinedTextField(
                    value = baseTitle,
                    onValueChange = { baseTitle = it },
                    label = { Text("Título de la(s) fotografía(s)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                CategoryDropdownField(
                    label = "Categoría",
                    selected = category,
                    options = categories.ifEmpty { listOf("Jornadas psicoeducativas") },
                    onSelected = { category = it }
                )

                if (uploadState.isUploading) {
                    LinearProgressIndicator(progress = { uploadState.progress }, modifier = Modifier.fillMaxWidth())
                    Text(
                        text = "Optimizando y guardando fotos... ${(uploadState.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (!uploadState.errorMessage.isNullOrBlank()) {
                    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small) {
                        Text(
                            text = uploadState.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPublish(selectedGalleryId, baseTitle, description, category, pickedUris) },
                enabled = !uploadState.isUploading,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Publicar fotos")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !uploadState.isUploading) { Text("Cancelar") }
        }
    )
}

@Composable
fun CreateGalleryDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onCreate: (title: String, description: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.firstOrNull() ?: "Jornadas psicoeducativas") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Galería Fotográfica", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre de la galería") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                CategoryDropdownField(
                    label = "Categoría",
                    selected = category,
                    options = categories.ifEmpty { listOf("Jornadas psicoeducativas") },
                    onSelected = { category = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onCreate(title, description, category) },
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Crear galería")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun ArticleEditorDialog(
    existing: ArticleEntity?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (
        existingId: Int?,
        title: String,
        category: String,
        intro: String,
        body: String,
        conclusions: String,
        references: String,
        featured: Boolean,
        coverUri: Uri?,
        existingCoverPath: String
    ) -> Unit
) {
    var title by remember(existing) { mutableStateOf(existing?.title ?: "") }
    var category by remember(existing) { mutableStateOf(existing?.category ?: categories.firstOrNull() ?: "Salud mental") }
    var intro by remember(existing) { mutableStateOf(existing?.introduction ?: "") }
    var body by remember(existing) { mutableStateOf(existing?.bodyContent ?: "") }
    var conclusions by remember(existing) { mutableStateOf(existing?.conclusions ?: "") }
    var references by remember(existing) { mutableStateOf(existing?.apaReferences ?: "") }
    var featured by remember(existing) { mutableStateOf(existing?.isFeatured ?: false) }
    var coverUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) coverUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Publicar Artículo" else "Editar Artículo") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                CategoryDropdownField(
                    label = "Categoría",
                    selected = category,
                    options = categories.ifEmpty { listOf("Salud mental") },
                    onSelected = { category = it }
                )
                OutlinedButton(
                    onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (coverUri != null) "Nueva portada seleccionada" else "Seleccionar imagen (opcional)")
                }
                OutlinedTextField(value = intro, onValueChange = { intro = it }, label = { Text("Introducción") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Desarrollo del contenido") }, minLines = 4, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = conclusions, onValueChange = { conclusions = it }, label = { Text("Conclusiones") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = references, onValueChange = { references = it }, label = { Text("Referencias APA 7") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = featured, onCheckedChange = { featured = it })
                    Text("Destacar en página de inicio", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        existing?.id,
                        title,
                        category,
                        intro,
                        body,
                        conclusions,
                        references,
                        featured,
                        coverUri,
                        existing?.coverImagePath ?: ""
                    )
                },
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Guardar artículo")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun UploadResourceDialog(
    categories: List<String>,
    maxDocMb: Int,
    uploadState: UploadTaskState,
    onDismiss: () -> Unit,
    onPublish: (title: String, desc: String, category: String, type: String, uri: Uri?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.firstOrNull() ?: "Inteligencia emocional") }
    var resType by remember { mutableStateOf("PDF") }
    var pickedDocUri by remember { mutableStateOf<Uri?>(null) }

    val docLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) pickedDocUri = uri
    }

    AlertDialog(
        onDismissRequest = { if (!uploadState.isUploading) onDismiss() },
        title = { Text("Subir Recurso Educativo / PDF") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Formatos permitidos: PDF, PNG, JPG · Máx $maxDocMb MB.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FilledTonalButton(
                    onClick = { docLauncher.launch(arrayOf("application/pdf", "image/png", "image/jpeg")) },
                    enabled = !uploadState.isUploading,
                    modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (pickedDocUri == null) "Seleccionar PDF o infografía del celular" else "Archivo listo: ${pickedDocUri?.lastPathSegment}")
                }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título del recurso") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                CategoryDropdownField(
                    label = "Categoría",
                    selected = category,
                    options = categories.ifEmpty { listOf("Inteligencia emocional") },
                    onSelected = { category = it }
                )
                CategoryDropdownField(
                    label = "Tipo de material",
                    selected = resType,
                    options = listOf("PDF", "Guía", "Ejercicio", "Respiración", "Infografía", "Material"),
                    onSelected = { resType = it }
                )
                if (uploadState.isUploading) {
                    LinearProgressIndicator(progress = { uploadState.progress }, modifier = Modifier.fillMaxWidth())
                }
                if (!uploadState.errorMessage.isNullOrBlank()) {
                    Text(uploadState.errorMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onPublish(title, description, category, resType, pickedDocUri) },
                enabled = !uploadState.isUploading,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Publicar recurso")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !uploadState.isUploading) { Text("Cancelar") } }
    )
}

@Composable
fun EditVideoMetadataDialog(
    video: VideoItemEntity,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (VideoItemEntity) -> Unit
) {
    var title by remember(video) { mutableStateOf(video.title) }
    var description by remember(video) { mutableStateOf(video.description) }
    var category by remember(video) { mutableStateOf(video.category) }
    var subtitles by remember(video) { mutableStateOf(video.subtitlesText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar video") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                CategoryDropdownField("Categoría", category, categories, onSelected = { category = it })
                OutlinedTextField(value = subtitles, onValueChange = { subtitles = it }, label = { Text("Subtítulos") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) {
                    onSave(video.copy(title = title.trim(), description = description.trim(), category = category, subtitlesText = subtitles.trim()))
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun EditPhotoMetadataDialog(
    photo: PhotoItemEntity,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (PhotoItemEntity) -> Unit
) {
    var title by remember(photo) { mutableStateOf(photo.title) }
    var description by remember(photo) { mutableStateOf(photo.description) }
    var category by remember(photo) { mutableStateOf(photo.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar fotografía") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                CategoryDropdownField("Categoría", category, categories, onSelected = { category = it })
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank()) {
                    onSave(photo.copy(title = title.trim(), description = description.trim(), category = category))
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
