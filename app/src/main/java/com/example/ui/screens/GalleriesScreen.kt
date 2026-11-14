package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.PhotoGalleryEntity
import com.example.data.model.PhotoItemEntity
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.EmptyLibraryCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.formatAcademicDate
import java.io.File

@Composable
fun GalleriesScreen(
    galleries: List<PhotoGalleryEntity>,
    photos: List<PhotoItemEntity>,
    galleryCategories: List<String>,
    onNavigateToAdminUpload: () -> Unit
) {
    var selectedCategory by rememberSaveable { mutableStateOf("Todas") }
    var zoomedPhoto by remember { mutableStateOf<PhotoItemEntity?>(null) }

    val filteredGalleries = galleries.filter {
        selectedCategory == "Todas" || it.category == selectedCategory
    }
    val standalonePhotos = photos.filter { p ->
        (selectedCategory == "Todas" || p.category == selectedCategory) &&
            galleries.none { g -> g.id == p.galleryId }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader(
                    title = "Galerías y Fotografías",
                    subtitle = "Material fotográfico educativo y registros visuales publicados desde Android"
                )
            }

            if (galleryCategories.isNotEmpty()) {
                item {
                    CategoryFilterRow(
                        categories = galleryCategories,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it }
                    )
                }
            }

            if (filteredGalleries.isEmpty() && standalonePhotos.isEmpty()) {
                item {
                    EmptyLibraryCard(
                        icon = Icons.Default.Collections,
                        title = "Aún no hay galerías fotográficas publicadas",
                        description = "El propietario puede crear galerías y subir múltiples fotografías reales desde su celular Android en el Panel de Administración.",
                        actionLabel = "Subir fotografías desde Admin",
                        onActionClick = onNavigateToAdminUpload
                    )
                }
            } else {
                items(filteredGalleries, key = { it.id }) { gallery ->
                    val galleryPhotos = photos.filter { it.galleryId == gallery.id }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gallery_card_${gallery.id}"),
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
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = gallery.category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = "${galleryPhotos.size} foto(s) · ${formatAcademicDate(gallery.createdAtMillis)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(text = gallery.title, style = MaterialTheme.typography.titleLarge)
                            if (gallery.description.isNotBlank()) {
                                Text(
                                    text = gallery.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (galleryPhotos.isEmpty()) {
                                Text(
                                    text = "Esta galería aún no tiene fotografías.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(galleryPhotos, key = { it.id }) { photo ->
                                        PhotoThumbnailCard(
                                            photo = photo,
                                            onClick = { zoomedPhoto = photo }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (standalonePhotos.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Fotografías individuales")
                        Spacer(Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(standalonePhotos, key = { it.id }) { photo ->
                                PhotoThumbnailCard(
                                    photo = photo,
                                    onClick = { zoomedPhoto = photo }
                                )
                            }
                        }
                    }
                }
            }

            item { DisclaimerBanner() }
        }
    }

    zoomedPhoto?.let { photo ->
        AlertDialog(
            onDismissRequest = { zoomedPhoto = null },
            title = { Text(photo.title, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AsyncImage(
                        model = File(photo.imagePath),
                        contentDescription = photo.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Text(
                        text = "${photo.category} · ${formatAcademicDate(photo.createdAtMillis)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    if (photo.description.isNotBlank()) {
                        Text(text = photo.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { zoomedPhoto = null },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun PhotoThumbnailCard(
    photo: PhotoItemEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .widthIn(min = 180.dp, max = 200.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            AsyncImage(
                model = File(photo.imagePath),
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                if (photo.description.isNotBlank()) {
                    Text(
                        text = photo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
