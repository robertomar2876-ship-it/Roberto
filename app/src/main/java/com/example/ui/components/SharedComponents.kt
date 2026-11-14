package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.preferences.AccessibilitySettings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AcademicBrainLogo(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val sageColor = MaterialTheme.colorScheme.tertiary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(10.dp),
        color = primaryColor
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .padding(6.dp)
        ) {
            val w = this.size.width
            val h = this.size.height

            // Stylized academic neural-book geometric emblem (strictly non-botanical)
            val leftArc = Path().apply {
                moveTo(w * 0.48f, h * 0.16f)
                cubicTo(w * 0.16f, h * 0.14f, w * 0.12f, h * 0.46f, w * 0.24f, h * 0.62f)
                cubicTo(w * 0.18f, h * 0.78f, w * 0.36f, h * 0.88f, w * 0.48f, h * 0.80f)
                lineTo(w * 0.48f, h * 0.16f)
            }
            drawPath(leftArc, color = onPrimaryColor, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))

            val rightPage = Path().apply {
                moveTo(w * 0.54f, h * 0.22f)
                lineTo(w * 0.84f, h * 0.16f)
                lineTo(w * 0.84f, h * 0.74f)
                lineTo(w * 0.54f, h * 0.82f)
                close()
            }
            drawPath(rightPage, color = sageColor, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round))

            // Cognitive nodes
            drawCircle(color = sageColor, radius = w * 0.07f, center = Offset(w * 0.32f, h * 0.42f))
            drawCircle(color = onPrimaryColor, radius = w * 0.06f, center = Offset(w * 0.69f, h * 0.48f))
            drawLine(
                color = onPrimaryColor,
                start = Offset(w * 0.32f, h * 0.42f),
                end = Offset(w * 0.69f, h * 0.48f),
                strokeWidth = w * 0.06f,
                cap = StrokeCap.Round
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PsicoTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    onOpenMenu: (() -> Unit)?,
    onOpenSearch: () -> Unit,
    onOpenAccessibility: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("top_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back)
                    )
                }
            } else if (onOpenMenu != null) {
                IconButton(
                    onClick = onOpenMenu,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("top_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Abrir menú de navegación"
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!canNavigateBack) {
                    AcademicBrainLogo(size = 32.dp)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { heading() }
                )
            }
        },
        actions = {
            IconButton(
                onClick = onOpenSearch,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.cd_global_search)
                )
            }
            IconButton(
                onClick = onOpenAccessibility,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_accessibility_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccessibilityNew,
                    contentDescription = stringResource(R.string.cd_accessibility_settings)
                )
            }
        }
    )
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() }
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(text = actionLabel)
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val allItems = listOf("Todas") + categories
        allItems.forEach { cat ->
            val selected = cat == selectedCategory
            FilterChip(
                selected = selected,
                onClick = { onSelectCategory(cat) },
                label = { Text(cat, style = MaterialTheme.typography.labelLarge) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.minimumInteractiveComponentSize()
            )
        }
    }
}

@Composable
fun DisclaimerBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Aviso de responsabilidad",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Aviso de responsabilidad psicoeducativa",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.disclaimer_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
fun EmptyLibraryCard(
    icon: ImageVector = Icons.Default.Psychology,
    title: String,
    description: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (actionLabel != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onActionClick() }
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AccessibilitySettingsDialog(
    settings: AccessibilitySettings,
    onFontScaleChange: (Float) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onDarkThemeModeChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.AccessibilityNew, contentDescription = null)
                Text("Accesibilidad visual", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Ajusta el tamaño de letra y el contraste para facilitar la lectura y compatibilidad con TalkBack.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tamaño de texto", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Normal" to 1.0f,
                            "Grande" to 1.15f,
                            "Muy grande" to 1.3f
                        ).forEach { (label, scale) ->
                            FilterChip(
                                selected = kotlin.math.abs(settings.fontScale - scale) < 0.05f,
                                onClick = { onFontScaleChange(scale) },
                                label = { Text(label) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Alto contraste", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Refuerza bordes y legibilidad de texto",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.highContrast,
                        onCheckedChange = onHighContrastChange,
                        modifier = Modifier.testTag("high_contrast_switch")
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Apariencia", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Sistema" to "system",
                            "Claro" to "light",
                            "Oscuro" to "dark"
                        ).forEach { (label, mode) ->
                            val isSelected = when (mode) {
                                "light" -> settings.darkThemePreference == false
                                "dark" -> settings.darkThemePreference == true
                                else -> settings.darkThemePreference == null
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { onDarkThemeModeChange(mode) },
                                label = { Text(label) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("Listo")
            }
        }
    )
}

fun formatAcademicDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("es-ES"))
    return formatter.format(Date(millis))
}
