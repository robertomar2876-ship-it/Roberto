package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ArticleEntity
import com.example.data.model.EducationalResourceEntity
import com.example.data.model.PhotoGalleryEntity
import com.example.data.model.VideoItemEntity
import com.example.ui.components.AcademicBrainLogo
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.SectionHeader
import com.example.ui.components.formatAcademicDate
import java.io.File

@Composable
fun HomeScreen(
    videos: List<VideoItemEntity>,
    articles: List<ArticleEntity>,
    resources: List<EducationalResourceEntity>,
    galleries: List<PhotoGalleryEntity>,
    disorderCount: Int,
    onNavigateToVideos: () -> Unit,
    onNavigateToDisorders: () -> Unit,
    onNavigateToArticles: () -> Unit,
    onNavigateToResources: () -> Unit,
    onNavigateToGalleries: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToContact: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onOpenArticle: (Int) -> Unit,
    onOpenVideo: (Int) -> Unit
) {
    val featuredArticles = articles.filter { it.isFeatured }.ifEmpty { articles.take(3) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_psychology_1794685032684),
                                contentDescription = stringResource(R.string.cd_hero_illustration),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AcademicBrainLogo(size = 34.dp)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.tertiaryContainer
                                    ) {
                                        Text(
                                            text = "DSM-5-TR · OMS · CIE-11",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.semantics { heading() }
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Proyecto Educativo Universitario · 9.º Semestre de Psicología",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = stringResource(R.string.welcome_message),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onNavigateToDisorders,
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("home_disorders_cta")
                                ) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Trastornos")
                                }
                                FilledTonalButton(
                                    onClick = onNavigateToVideos,
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .testTag("home_videos_cta")
                                ) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Videos")
                                }
                            }
                        }
                    }
                }
            }

            // Chatbot Psicoeducativo Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToChatbot() }
                        .testTag("home_chatbot_cta"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Chatbot Psicoeducativo",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Resuelve dudas sobre emociones, manejo del estrés y guías DSM-5-TR / OMS en tiempo real.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        FilledTonalButton(
                            onClick = onNavigateToChatbot,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Text("Abrir")
                        }
                    }
                }
            }

            // Quick Module Navigation Grid
            item {
                SectionHeader(
                    title = "Explora la biblioteca",
                    subtitle = "Acceso libre para visitantes sin necesidad de registro"
                )
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HomeQuickTile(
                            icon = Icons.Default.Psychology,
                            title = "DSM-5-TR Completo",
                            countLabel = "22 capítulos · $disorderCount guías",
                            onClick = onNavigateToDisorders,
                            modifier = Modifier.weight(1f).testTag("tile_disorders")
                        )
                        HomeQuickTile(
                            icon = Icons.Default.PlayCircle,
                            title = "Videos educativos",
                            countLabel = "${videos.size} publicados",
                            onClick = onNavigateToVideos,
                            modifier = Modifier.weight(1f).testTag("tile_videos")
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HomeQuickTile(
                            icon = Icons.Default.Article,
                            title = "Artículos científicos",
                            countLabel = "${articles.size} lecturas APA 7",
                            onClick = onNavigateToArticles,
                            modifier = Modifier.weight(1f).testTag("tile_articles")
                        )
                        HomeQuickTile(
                            icon = Icons.Default.Description,
                            title = "Recursos y PDF",
                            countLabel = "${resources.size} guías descargables",
                            onClick = onNavigateToResources,
                            modifier = Modifier.weight(1f).testTag("tile_resources")
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HomeQuickTile(
                            icon = Icons.Default.Collections,
                            title = "Galerías fotográficas",
                            countLabel = "${galleries.size} colecciones",
                            onClick = onNavigateToGalleries,
                            modifier = Modifier.weight(1f).testTag("tile_galleries")
                        )
                        HomeQuickTile(
                            icon = Icons.Default.AdminPanelSettings,
                            title = "Panel del creador",
                            countLabel = "Subir fotos y videos",
                            onClick = onNavigateToAdmin,
                            modifier = Modifier.weight(1f).testTag("tile_admin")
                        )
                    }
                }
            }

            // Featured Articles
            if (featuredArticles.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Artículos destacados",
                        subtitle = "Psicoeducación rigurosa y comprensible",
                        actionLabel = "Ver todos",
                        onActionClick = onNavigateToArticles
                    )
                    Spacer(Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(featuredArticles, key = { it.id }) { article ->
                            Card(
                                modifier = Modifier
                                    .width(280.dp)
                                    .clickable { onOpenArticle(article.id) }
                                    .testTag("featured_article_${article.id}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column {
                                    ArticleThumbnail(article = article, modifier = Modifier.fillMaxWidth().height(126.dp))
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = article.category,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                        Text(
                                            text = article.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = article.introduction,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Latest Publications
            item {
                SectionHeader(
                    title = "Últimas publicaciones",
                    subtitle = "Contenidos recientes en la plataforma"
                )
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    videos.take(2).forEach { video ->
                        LatestPublicationRow(
                            badge = "Video · ${video.category}",
                            title = video.title,
                            dateLabel = formatAcademicDate(video.publishedAtMillis),
                            icon = Icons.Default.PlayCircle,
                            onClick = { onOpenVideo(video.id) }
                        )
                    }
                    articles.take(2).forEach { article ->
                        LatestPublicationRow(
                            badge = "Artículo · ${article.category}",
                            title = article.title,
                            dateLabel = formatAcademicDate(article.publishedAtMillis),
                            icon = Icons.Default.Article,
                            onClick = { onOpenArticle(article.id) }
                        )
                    }
                    resources.take(2).forEach { res ->
                        LatestPublicationRow(
                            badge = "${res.resourceType} · ${res.category}",
                            title = res.title,
                            dateLabel = formatAcademicDate(res.publishedAtMillis),
                            icon = Icons.Default.Description,
                            onClick = onNavigateToResources
                        )
                    }
                }
            }

            // Creator Intro Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = stringResource(R.string.creator_name),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Creador del proyecto · Estudiante de 9.º semestre de Psicología",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.creator_presentation_p1),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = onNavigateToAbout,
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text("Conocer más")
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                            OutlinedButton(
                                onClick = onNavigateToContact,
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Contacto")
                            }
                        }
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
private fun HomeQuickTile(
    icon: ImageVector,
    title: String,
    countLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = countLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LatestPublicationRow(
    badge: String,
    title: String,
    dateLabel: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Abrir publicación",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ArticleThumbnail(
    article: ArticleEntity,
    modifier: Modifier = Modifier
) {
    if (article.coverImagePath.isNotBlank() && File(article.coverImagePath).exists()) {
        AsyncImage(
            model = File(article.coverImagePath),
            contentDescription = article.title,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
        )
    } else {
        val resId = when (article.bundledIllustrationKey) {
            "balance" -> R.drawable.img_emotional_balance_1794685033252
            "hero" -> R.drawable.img_hero_psychology_1794685032684
            else -> R.drawable.img_brain_cognition_1794685032895
        }
        Image(
            painter = painterResource(id = resId),
            contentDescription = article.title,
            contentScale = ContentScale.Crop,
            modifier = modifier.clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
        )
    }
}
