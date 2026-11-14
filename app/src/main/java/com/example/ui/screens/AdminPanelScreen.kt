package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.model.ArticleEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ContactMessageEntity
import com.example.data.model.EducationalResourceEntity
import com.example.data.model.PhotoGalleryEntity
import com.example.data.model.PhotoItemEntity
import com.example.data.model.VideoItemEntity
import com.example.data.preferences.AdminAuthState
import com.example.data.preferences.StorageLimitsConfig
import com.example.data.storage.MediaStorageManager
import com.example.data.storage.StorageUsageSummary
import com.example.ui.components.SectionHeader
import com.example.ui.components.formatAcademicDate
import com.example.ui.viewmodel.AdminSessionState
import com.example.ui.viewmodel.UploadTaskState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    authState: AdminAuthState,
    sessionState: AdminSessionState,
    uploadState: UploadTaskState,
    storageLimits: StorageLimitsConfig,
    storageUsage: StorageUsageSummary,
    videos: List<VideoItemEntity>,
    galleries: List<PhotoGalleryEntity>,
    photos: List<PhotoItemEntity>,
    articles: List<ArticleEntity>,
    resources: List<EducationalResourceEntity>,
    categories: List<CategoryEntity>,
    contactMessages: List<ContactMessageEntity>,
    onSetupAdmin: (displayName: String, password: String) -> Unit,
    onLoginAdmin: (password: String) -> Unit,
    onLogoutAdmin: () -> Unit,
    onClearAuthError: () -> Unit,
    onClearUploadFeedback: () -> Unit,
    onUploadVideo: (String, String, String, String, Uri?, String, Uri?, () -> Unit) -> Unit,
    onUpdateVideo: (VideoItemEntity) -> Unit,
    onDeleteVideo: (VideoItemEntity) -> Unit,
    onCreateGallery: (String, String, String) -> Unit,
    onDeleteGallery: (PhotoGalleryEntity) -> Unit,
    onUploadPhotos: (Int, String, String, String, List<Uri>, () -> Unit) -> Unit,
    onUpdatePhoto: (PhotoItemEntity) -> Unit,
    onDeletePhoto: (PhotoItemEntity) -> Unit,
    onSaveArticle: (Int?, String, String, String, String, String, String, Boolean, Uri?, String, () -> Unit) -> Unit,
    onDeleteArticle: (ArticleEntity) -> Unit,
    onUploadResource: (String, String, String, String, Uri?, () -> Unit) -> Unit,
    onDeleteResource: (EducationalResourceEntity) -> Unit,
    onAddCategory: (String, String) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit,
    onUpdateStorageLimits: (Int, Int, Int) -> Unit,
    onMarkMessageRead: (Int) -> Unit,
    onDeleteMessage: (Int) -> Unit
) {
    if (!sessionState.isAuthenticated) {
        AdminLoginGate(
            isConfigured = authState.isConfigured,
            authError = sessionState.authError,
            onSetup = onSetupAdmin,
            onLogin = onLoginAdmin,
            onClearError = onClearAuthError
        )
        return
    }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showVideoDialog by remember { mutableStateOf(false) }
    var showPhotoDialog by remember { mutableStateOf(false) }
    var showGalleryDialog by remember { mutableStateOf(false) }
    var showResourceDialog by remember { mutableStateOf(false) }
    var editingArticle by remember { mutableStateOf<ArticleEntity?>(null) }
    var showNewArticleDialog by remember { mutableStateOf(false) }
    var editingVideo by remember { mutableStateOf<VideoItemEntity?>(null) }
    var editingPhoto by remember { mutableStateOf<PhotoItemEntity?>(null) }

    val videoCats = categories.filter { it.scope == "VIDEO" }.map { it.name }
    val galleryCats = categories.filter { it.scope == "GALLERY" }.map { it.name }
    val articleCats = categories.filter { it.scope == "ARTICLE" }.map { it.name }
    val resourceCats = categories.filter { it.scope == "RESOURCE" }.map { it.name }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 900.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header + Logout
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Panel Privado de Administración",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.semantics { heading() }
                        )
                        Text(
                            text = "Propietario: ${authState.adminDisplayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    OutlinedButton(
                        onClick = onLogoutAdmin,
                        modifier = Modifier.minimumInteractiveComponentSize().testTag("admin_logout_button")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Cerrar sesión")
                    }
                }
            }

            // Status / Error Banner
            if (!uploadState.statusMessage.isNullOrBlank()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uploadState.statusMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = onClearUploadFeedback) { Text("OK") }
                        }
                    }
                }
            }

            // 4 Mandatory Quick Upload & Management Buttons
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SectionHeader(
                            title = "Módulo Multimedia Android",
                            subtitle = "Sube videos y fotografías directamente desde tu celular"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onClearUploadFeedback(); showVideoDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("admin_btn_upload_video")
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("SUBIR VIDEO")
                            }
                            Button(
                                onClick = { onClearUploadFeedback(); showPhotoDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("admin_btn_upload_photo")
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("SUBIR FOTOGRAFÍA")
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { onClearUploadFeedback(); showGalleryDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("admin_btn_create_gallery")
                            ) {
                                Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("CREAR GALERÍA")
                            }
                            OutlinedButton(
                                onClick = { selectedTab = 1 },
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("admin_btn_view_published")
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("VER CONTENIDO PUBLICADO")
                            }
                        }
                    }
                }
            }

            // Basic Stats Summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminStatCard("Videos", videos.size.toString(), Modifier.weight(1f))
                    AdminStatCard("Fotos", photos.size.toString(), Modifier.weight(1f))
                    AdminStatCard("Artículos", articles.size.toString(), Modifier.weight(1f))
                    AdminStatCard("PDF/Guías", resources.size.toString(), Modifier.weight(1f))
                }
            }

            // Navigation Tabs inside Admin
            item {
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Publicar") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Contenido") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Categorías") })
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Ajustes/Buzón") })
                }
            }

            when (selectedTab) {
                0 -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SectionHeader(title = "Publicar Artículos y Documentos PDF")
                                Button(
                                    onClick = { showNewArticleDialog = true },
                                    modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Default.Article, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Nuevo artículo científico")
                                }
                                FilledTonalButton(
                                    onClick = { onClearUploadFeedback(); showResourceDialog = true },
                                    modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Subir archivo PDF o infografía educativa")
                                }
                            }
                        }
                    }
                    item {
                        StorageOverviewCard(
                            storageUsage = storageUsage,
                            limits = storageLimits,
                            onUpdateLimits = onUpdateStorageLimits
                        )
                    }
                }

                1 -> {
                    // Published Videos
                    item { SectionHeader(title = "Videos publicados (${videos.size})") }
                    items(videos, key = { "v_${it.id}" }) { video ->
                        AdminManagedItemRow(
                            title = video.title,
                            subtitle = "${video.category} · ${if (video.isExternalUrl) "Enlace externo" else MediaStorageManager.formatSize(video.fileSizeBytes)}",
                            onEdit = { editingVideo = video },
                            onDelete = { onDeleteVideo(video) }
                        )
                    }

                    // Galleries & Photos
                    item { SectionHeader(title = "Galerías (${galleries.size}) y Fotografías (${photos.size})") }
                    items(galleries, key = { "g_${it.id}" }) { gallery ->
                        AdminManagedItemRow(
                            title = "Galería: ${gallery.title}",
                            subtitle = "${gallery.category} · ${photos.count { it.galleryId == gallery.id }} foto(s)",
                            onEdit = null,
                            onDelete = { onDeleteGallery(gallery) }
                        )
                    }
                    items(photos, key = { "p_${it.id}" }) { photo ->
                        AdminManagedItemRow(
                            title = photo.title,
                            subtitle = "${photo.category} · ${MediaStorageManager.formatSize(photo.fileSizeBytes)}",
                            onEdit = { editingPhoto = photo },
                            onDelete = { onDeletePhoto(photo) }
                        )
                    }

                    // Articles
                    item { SectionHeader(title = "Artículos (${articles.size})") }
                    items(articles, key = { "a_${it.id}" }) { article ->
                        AdminManagedItemRow(
                            title = article.title,
                            subtitle = "${article.category} · ${formatAcademicDate(article.publishedAtMillis)}",
                            onEdit = { editingArticle = article },
                            onDelete = { onDeleteArticle(article) }
                        )
                    }

                    // Resources
                    item { SectionHeader(title = "Recursos educativos y PDF (${resources.size})") }
                    items(resources, key = { "r_${it.id}" }) { res ->
                        AdminManagedItemRow(
                            title = res.title,
                            subtitle = "${res.resourceType} · ${res.category} · ${MediaStorageManager.formatSize(res.fileSizeBytes)}",
                            onEdit = null,
                            onDelete = { onDeleteResource(res) }
                        )
                    }
                }

                2 -> {
                    item {
                        CategoryManagerCard(
                            categories = categories,
                            onAddCategory = onAddCategory,
                            onDeleteCategory = onDeleteCategory
                        )
                    }
                }

                3 -> {
                    item {
                        StorageOverviewCard(
                            storageUsage = storageUsage,
                            limits = storageLimits,
                            onUpdateLimits = onUpdateStorageLimits
                        )
                    }
                    item {
                        SectionHeader(
                            title = "Mensajes de contacto (${contactMessages.size})",
                            subtitle = "Consultas recibidas desde el formulario público"
                        )
                    }
                    if (contactMessages.isEmpty()) {
                        item {
                            Text(
                                text = "Aún no hay mensajes recibidos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(contactMessages, key = { "m_${it.id}" }) { msg ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${msg.senderName} (${msg.senderEmail})",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Row {
                                            if (!msg.isRead) {
                                                IconButton(onClick = { onMarkMessageRead(msg.id) }) {
                                                    Icon(Icons.Default.MarkEmailRead, contentDescription = "Marcar leído")
                                                }
                                            }
                                            IconButton(onClick = { onDeleteMessage(msg.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar mensaje")
                                            }
                                        }
                                    }
                                    Text("Asunto: ${msg.subject}", style = MaterialTheme.typography.labelLarge)
                                    Text(msg.message, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = formatAcademicDate(msg.submittedAtMillis) + if (msg.isRead) " · Leído" else " · Nuevo",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showVideoDialog) {
        UploadVideoDialog(
            categories = videoCats,
            maxVideoMb = storageLimits.maxVideoMb,
            uploadState = uploadState,
            onDismiss = { showVideoDialog = false; onClearUploadFeedback() },
            onPublish = { t, d, c, sub, uri, ext, cover ->
                onUploadVideo(t, d, c, sub, uri, ext, cover) { showVideoDialog = false }
            }
        )
    }

    if (showPhotoDialog) {
        UploadPhotosDialog(
            galleries = galleries,
            categories = galleryCats,
            maxPhotoMb = storageLimits.maxPhotoMb,
            uploadState = uploadState,
            onDismiss = { showPhotoDialog = false; onClearUploadFeedback() },
            onQuickCreateGallery = { gTitle, gCat -> onCreateGallery(gTitle, "Galería de fotografías", gCat) },
            onPublish = { gId, baseTitle, desc, cat, uris ->
                onUploadPhotos(gId, baseTitle, desc, cat, uris) { showPhotoDialog = false }
            }
        )
    }

    if (showGalleryDialog) {
        CreateGalleryDialog(
            categories = galleryCats,
            onDismiss = { showGalleryDialog = false },
            onCreate = { t, d, c ->
                onCreateGallery(t, d, c)
                showGalleryDialog = false
            }
        )
    }

    if (showNewArticleDialog || editingArticle != null) {
        ArticleEditorDialog(
            existing = editingArticle,
            categories = articleCats,
            onDismiss = { showNewArticleDialog = false; editingArticle = null },
            onSave = { id, t, c, intro, body, conc, ref, feat, coverUri, existingCover ->
                onSaveArticle(id, t, c, intro, body, conc, ref, feat, coverUri, existingCover) {
                    showNewArticleDialog = false
                    editingArticle = null
                }
            }
        )
    }

    if (showResourceDialog) {
        UploadResourceDialog(
            categories = resourceCats,
            maxDocMb = storageLimits.maxDocumentMb,
            uploadState = uploadState,
            onDismiss = { showResourceDialog = false; onClearUploadFeedback() },
            onPublish = { t, d, c, type, uri ->
                onUploadResource(t, d, c, type, uri) { showResourceDialog = false }
            }
        )
    }

    editingVideo?.let { vid ->
        EditVideoMetadataDialog(
            video = vid,
            categories = videoCats,
            onDismiss = { editingVideo = null },
            onSave = { updated -> onUpdateVideo(updated); editingVideo = null }
        )
    }

    editingPhoto?.let { pho ->
        EditPhotoMetadataDialog(
            photo = pho,
            categories = galleryCats,
            onDismiss = { editingPhoto = null },
            onSave = { updated -> onUpdatePhoto(updated); editingPhoto = null }
        )
    }
}

@Composable
private fun AdminLoginGate(
    isConfigured: Boolean,
    authError: String?,
    onSetup: (String, String) -> Unit,
    onLogin: (String) -> Unit,
    onClearError: () -> Unit
) {
    var displayName by rememberSaveable { mutableStateOf("Roberto Martínez Paternina") }
    var password by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.widthIn(max = 460.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Column {
                        Text(
                            text = if (isConfigured) "Acceso de Administrador" else "Configurar Acceso del Propietario",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = if (isConfigured) {
                                "Área privada para publicar y gestionar contenidos"
                            } else {
                                "Crea una clave privada (mín. 6 caracteres) para proteger tu panel"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!isConfigured) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Nombre del propietario / autor") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; onClearError() },
                    label = { Text("Clave de administración") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_password_input")
                )

                if (!authError.isNullOrBlank()) {
                    Text(
                        text = authError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        if (isConfigured) onLogin(password) else onSetup(displayName, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("admin_login_submit_btn")
                ) {
                    Text(if (isConfigured) "Ingresar al panel" else "Guardar clave e ingresar")
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(label: String, count: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AdminManagedItemRow(
    title: String,
    subtitle: String,
    onEdit: (() -> Unit)?,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (onEdit != null) {
                IconButton(onClick = onEdit, modifier = Modifier.minimumInteractiveComponentSize()) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.minimumInteractiveComponentSize()) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun CategoryManagerCard(
    categories: List<CategoryEntity>,
    onAddCategory: (String, String) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit
) {
    var newName by remember { mutableStateOf("") }
    var scope by remember { mutableStateOf("VIDEO") }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(title = "Administrar categorías (${categories.size})")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Nueva categoría") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onAddCategory(newName, scope)
                            newName = ""
                        }
                    },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar categoría")
                }
            }
            CategoryDropdownField(
                label = "Módulo",
                selected = scope,
                options = listOf("VIDEO", "ARTICLE", "GALLERY", "RESOURCE"),
                onSelected = { scope = it }
            )
            categories.forEach { cat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${cat.scope} · ${cat.name}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    if (!cat.isSystemDefault) {
                        IconButton(onClick = { onDeleteCategory(cat) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar categoría")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StorageOverviewCard(
    storageUsage: StorageUsageSummary,
    limits: StorageLimitsConfig,
    onUpdateLimits: (Int, Int, Int) -> Unit
) {
    var videoMbText by remember(limits.maxVideoMb) { mutableStateOf(limits.maxVideoMb.toString()) }
    var photoMbText by remember(limits.maxPhotoMb) { mutableStateOf(limits.maxPhotoMb.toString()) }
    var docMbText by remember(limits.maxDocumentMb) { mutableStateOf(limits.maxDocumentMb.toString()) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(
                title = "Almacenamiento local y límites",
                subtitle = "Uso actual: ${MediaStorageManager.formatSize(storageUsage.totalUsedBytes)} · Disponible: ${MediaStorageManager.formatSize(storageUsage.availableDeviceBytes)}"
            )
            Text(
                text = "Videos: ${MediaStorageManager.formatSize(storageUsage.videosBytes)} · Fotos: ${MediaStorageManager.formatSize(storageUsage.photosBytes)} · PDF/Docs: ${MediaStorageManager.formatSize(storageUsage.docsBytes)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = videoMbText,
                    onValueChange = { videoMbText = it.filter(Char::isDigit) },
                    label = { Text("Máx Video MB") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = photoMbText,
                    onValueChange = { photoMbText = it.filter(Char::isDigit) },
                    label = { Text("Máx Foto MB") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = docMbText,
                    onValueChange = { docMbText = it.filter(Char::isDigit) },
                    label = { Text("Máx PDF MB") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedButton(
                onClick = {
                    onUpdateLimits(
                        videoMbText.toIntOrNull() ?: limits.maxVideoMb,
                        photoMbText.toIntOrNull() ?: limits.maxPhotoMb,
                        docMbText.toIntOrNull() ?: limits.maxDocumentMb
                    )
                },
                modifier = Modifier.fillMaxWidth().minimumInteractiveComponentSize()
            ) {
                Text("Guardar límites de carga")
            }
        }
    }
}
