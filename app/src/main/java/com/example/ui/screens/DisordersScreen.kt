package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.MentalDisorderEntity
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.SectionHeader

private enum class DsmGroupFilter(val label: String) {
    ALL("Todos (22 cap.)"),
    DSM5TR_UPDATES("Novedades DSM-5-TR"),
    MOOD_ANXIETY("Ánimo, Ansiedad y TOC"),
    NEURO("Neurodesarrollo y Cognición"),
    TRAUMA_BODY("Trauma, Disociación y Cuerpo"),
    IMPULSE_CLINICAL("Conducta, Personalidad y Clínica")
}

@Composable
fun DisordersLibraryScreen(
    disorders: List<MentalDisorderEntity>,
    onSelectDisorder: (Int) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedFilterName by rememberSaveable { mutableStateOf(DsmGroupFilter.ALL.name) }
    val selectedFilter = DsmGroupFilter.entries.firstOrNull { it.name == selectedFilterName } ?: DsmGroupFilter.ALL

    val filtered = disorders.filter { d ->
        val matchesGroup = when (selectedFilter) {
            DsmGroupFilter.ALL -> true
            DsmGroupFilter.DSM5TR_UPDATES ->
                d.keywords.contains("novedad dsm-5-tr", ignoreCase = true) ||
                    d.categoryNumber in listOf(7, 14, 22)
            DsmGroupFilter.MOOD_ANXIETY -> d.categoryNumber in listOf(3, 4, 5, 6)
            DsmGroupFilter.NEURO -> d.categoryNumber in listOf(1, 2, 17)
            DsmGroupFilter.TRAUMA_BODY -> d.categoryNumber in listOf(7, 8, 9, 10, 11, 12, 13, 14)
            DsmGroupFilter.IMPULSE_CLINICAL -> d.categoryNumber in 15..22
        }
        val matchesQuery = query.isBlank() ||
            d.categoryName.contains(query, ignoreCase = true) ||
            d.title.contains(query, ignoreCase = true) ||
            d.summary.contains(query, ignoreCase = true) ||
            d.mainCharacteristics.contains(query, ignoreCase = true) ||
            d.keywords.contains(query, ignoreCase = true) ||
            "capítulo ${d.categoryNumber}".contains(query, ignoreCase = true)
        matchesGroup && matchesQuery
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_brain_cognition_1794685032895),
                            contentDescription = stringResource(R.string.cd_brain_illustration),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(82.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "DSM-5-TR Completo (22 Capítulos)",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.semantics { heading() }
                            )
                            Text(
                                text = "Síntesis educativa completa basada en DSM-5-TR (APA, 2022), OMS y CIE-11, sin reproducción protegida.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NewReleases,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Actualizaciones clave del DSM-5-TR (2022)",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Text(
                            text = "• Incorporación del Trastorno de duelo prolongado (Cap. 7).\n• Códigos específicos para conducta suicida y autolesión no suicida (Cap. 22).\n• Lenguaje clínico actualizado, inclusivo y libre de estigma (Cap. 1, 14 y 22) + revisión cultural y CIE-11.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar capítulo, trastorno o síntoma") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("disorders_search_input")
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DsmGroupFilter.entries.forEach { group ->
                        FilterChip(
                            selected = selectedFilter == group,
                            onClick = { selectedFilterName = group.name },
                            label = { Text(group.label) },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Mostrando ${filtered.size} de ${disorders.size} guías clínicas educativas",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(filtered, key = { it.id }) { disorder ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectDisorder(disorder.id) }
                        .testTag("disorder_card_${disorder.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Text(
                                    text = "Cap. ${disorder.categoryNumber}. ${disorder.categoryName}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Ver detalle",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = disorder.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = disorder.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = disorder.dsmReference,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            item {
                DisclaimerBanner()
            }
        }
    }
}

@Composable
fun DisorderDetailScreen(
    disorder: MentalDisorderEntity?
) {
    if (disorder == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se encontró el tema seleccionado.", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${disorder.categoryNumber}. ${disorder.categoryName} · ${disorder.dsmReference}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = disorder.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.semantics { heading() }
                )
            }

            item {
                DisorderInfoSection(
                    title = "Definición en lenguaje sencillo",
                    content = disorder.summary
                )
            }
            item {
                DisorderInfoSection(
                    title = "Características principales",
                    content = disorder.mainCharacteristics
                )
            }
            item {
                DisorderInfoSection(
                    title = "Manifestaciones y síntomas frecuentes",
                    content = disorder.manifestationsSymptoms
                )
            }
            item {
                DisorderInfoSection(
                    title = "Factores de riesgo y asociados",
                    content = disorder.riskFactors
                )
            }
            item {
                DisorderInfoSection(
                    title = "Impacto en la vida cotidiana",
                    content = disorder.dailyImpact
                )
            }
            item {
                DisorderInfoSection(
                    title = "Alternativas de tratamiento reconocidas",
                    content = disorder.treatmentOptions
                )
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "¿Cuándo buscar ayuda profesional?",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.semantics { heading() }
                        )
                        Text(
                            text = disorder.whenToSeekHelp,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            SectionHeader(title = "Referencias bibliográficas (APA 7)")
                        }
                        Text(
                            text = disorder.apaReferences,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                DisclaimerBanner()
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun DisorderInfoSection(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
