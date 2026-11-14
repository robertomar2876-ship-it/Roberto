package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.ArticleEntity
import com.example.data.model.EducationalResourceEntity
import com.example.data.model.MentalDisorderEntity
import com.example.data.model.PhotoItemEntity
import com.example.data.model.VideoItemEntity
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.EmptyLibraryCard
import com.example.ui.components.SectionHeader

private data class SearchResultRowData(
    val id: String,
    val typeLabel: String,
    val category: String,
    val title: String,
    val snippet: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun SearchScreen(
    disorders: List<MentalDisorderEntity>,
    videos: List<VideoItemEntity>,
    articles: List<ArticleEntity>,
    photos: List<PhotoItemEntity>,
    resources: List<EducationalResourceEntity>,
    allCategoryNames: List<String>,
    onOpenDisorder: (Int) -> Unit,
    onOpenVideo: (Int) -> Unit,
    onOpenArticle: (Int) -> Unit,
    onOpenGalleries: () -> Unit,
    onOpenResources: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("Todos") }
    var selectedCategory by rememberSaveable { mutableStateOf("Todas") }

    val combined = buildList {
        disorders.forEach { d ->
            add(
                SearchResultRowData(
                    id = "d_${d.id}",
                    typeLabel = "Trastornos",
                    category = d.categoryName,
                    title = d.title,
                    snippet = d.summary,
                    icon = Icons.Default.Psychology,
                    onClick = { onOpenDisorder(d.id) }
                )
            )
        }
        videos.forEach { v ->
            add(
                SearchResultRowData(
                    id = "v_${v.id}",
                    typeLabel = "Videos",
                    category = v.category,
                    title = v.title,
                    snippet = v.description.ifBlank { "Video psicoeducativo" },
                    icon = Icons.Default.PlayCircle,
                    onClick = { onOpenVideo(v.id) }
                )
            )
        }
        articles.forEach { a ->
            add(
                SearchResultRowData(
                    id = "a_${a.id}",
                    typeLabel = "Artículos",
                    category = a.category,
                    title = a.title,
                    snippet = a.introduction,
                    icon = Icons.Default.Article,
                    onClick = { onOpenArticle(a.id) }
                )
            )
        }
        photos.forEach { p ->
            add(
                SearchResultRowData(
                    id = "p_${p.id}",
                    typeLabel = "Fotografías",
                    category = p.category,
                    title = p.title,
                    snippet = p.description.ifBlank { "Fotografía publicada en galería" },
                    icon = Icons.Default.Collections,
                    onClick = onOpenGalleries
                )
            )
        }
        resources.forEach { r ->
            add(
                SearchResultRowData(
                    id = "r_${r.id}",
                    typeLabel = "Recursos",
                    category = r.category,
                    title = r.title,
                    snippet = r.description,
                    icon = Icons.Default.Description,
                    onClick = onOpenResources
                )
            )
        }
    }

    val filtered = combined.filter { item ->
        val matchesType = selectedType == "Todos" || item.typeLabel == selectedType
        val matchesCategory = selectedCategory == "Todas" || item.category.equals(selectedCategory, ignoreCase = true)
        val matchesQuery = query.isBlank() ||
            item.title.contains(query, ignoreCase = true) ||
            item.snippet.contains(query, ignoreCase = true) ||
            item.category.contains(query, ignoreCase = true)
        matchesType && matchesCategory && matchesQuery
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader(
                    title = "Buscador General",
                    subtitle = "Encuentra trastornos, videos, artículos, fotografías y recursos educativos"
                )
            }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("¿Qué tema de psicología deseas consultar?") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("global_search_input")
                )
            }

            item {
                CategoryFilterRow(
                    categories = listOf("Trastornos", "Videos", "Artículos", "Fotografías", "Recursos"),
                    selectedCategory = if (selectedType == "Todos") "Todas" else selectedType,
                    onSelectCategory = { selectedType = if (it == "Todas") "Todos" else it }
                )
            }

            if (allCategoryNames.isNotEmpty()) {
                item {
                    CategoryFilterRow(
                        categories = allCategoryNames.distinct(),
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it }
                    )
                }
            }

            item {
                Text(
                    text = "${filtered.size} resultado(s)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (filtered.isEmpty()) {
                item {
                    EmptyLibraryCard(
                        icon = Icons.Default.Search,
                        title = "Sin coincidencias",
                        description = "Intenta buscar con otras palabras clave o restablece los filtros."
                    )
                }
            } else {
                items(filtered, key = { it.id }) { row ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { row.onClick() },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = row.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = row.typeLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = row.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = row.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = row.snippet,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Abrir resultado",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
