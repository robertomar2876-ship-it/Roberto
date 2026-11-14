package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.EducationalResourceEntity
import com.example.data.storage.MediaStorageManager
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.EmptyLibraryCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.formatAcademicDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ResourcesScreen(
    resources: List<EducationalResourceEntity>,
    categories: List<String>,
    storageManager: MediaStorageManager
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedCategory by rememberSaveable { mutableStateOf("Todas") }
    var previewResource by remember { mutableStateOf<EducationalResourceEntity?>(null) }
    var resourceToExport by remember { mutableStateOf<EducationalResourceEntity?>(null) }

    val saveDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { destUri ->
        val res = resourceToExport
        if (destUri != null && res != null && res.filePath.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                val ok = runCatching {
                    File(res.filePath).inputStream().use { input ->
                        context.contentResolver.openOutputStream(destUri)?.use { output ->
                            input.copyTo(output)
                        }
                    }
                    true
                }.getOrDefault(false)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        if (ok) "Documento guardado en tu dispositivo." else "No se pudo guardar el archivo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    val filtered = resources.filter { selectedCategory == "Todas" || it.category == selectedCategory }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SectionHeader(
                    title = "Recursos Educativos",
                    subtitle = "Guías descargables en PDF, ejercicios prácticos, técnicas de respiración e infografías"
                )
            }

            item {
                CategoryFilterRow(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it }
                )
            }

            if (filtered.isEmpty()) {
                item {
                    EmptyLibraryCard(
                        icon = Icons.Default.Description,
                        title = "No hay recursos en esta categoría",
                        description = "Selecciona otra categoría para explorar las guías y documentos disponibles."
                    )
                }
            } else {
                items(filtered, key = { it.id }) { resource ->
                    ResourceCard(
                        resource = resource,
                        onPreview = { previewResource = resource },
                        onDownload = {
                            if (resource.mimeType.contains("pdf")) {
                                resourceToExport = resource
                                val safeName = resource.title.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_') + ".pdf"
                                saveDocumentLauncher.launch(safeName)
                            } else {
                                previewResource = resource
                            }
                        },
                        onShare = {
                            val uri = storageManager.getShareableUri(resource.filePath)
                            if (uri != null) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = resource.mimeType
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Compartir material psicoeducativo"))
                            } else {
                                Toast.makeText(context, "Archivo no disponible para compartir.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            item { DisclaimerBanner() }
        }
    }

    previewResource?.let { res ->
        ResourcePreviewDialog(
            resource = res,
            storageManager = storageManager,
            onDismiss = { previewResource = null }
        )
    }
}

@Composable
private fun ResourceCard(
    resource: EducationalResourceEntity,
    onPreview: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("resource_card_${resource.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    ) {
                        Text(
                            text = resource.resourceType,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = resource.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = MediaStorageManager.formatSize(resource.fileSizeBytes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(text = resource.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Publicado: ${formatAcademicDate(resource.publishedAtMillis)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onPreview,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Leer")
                }
                Button(
                    onClick = onDownload,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar")
                }
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Compartir documento", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun ResourcePreviewDialog(
    resource: EducationalResourceEntity,
    storageManager: MediaStorageManager,
    onDismiss: () -> Unit
) {
    var pdfBitmap by remember(resource.filePath) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(resource.filePath) {
        if (resource.mimeType.contains("pdf") && resource.filePath.isNotBlank()) {
            pdfBitmap = storageManager.renderPdfFirstPagePreview(resource.filePath)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(resource.title, style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (resource.mimeType.startsWith("image/") && resource.filePath.isNotBlank()) {
                    AsyncImage(
                        model = File(resource.filePath),
                        contentDescription = resource.title,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else if (pdfBitmap != null) {
                    Image(
                        bitmap = pdfBitmap!!.asImageBitmap(),
                        contentDescription = "Vista previa de la primera página de ${resource.title}",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
                if (resource.contentSummary.isNotBlank()) {
                    Text(
                        text = resource.contentSummary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = resource.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.minimumInteractiveComponentSize()) {
                Text("Cerrar")
            }
        }
    )
}
