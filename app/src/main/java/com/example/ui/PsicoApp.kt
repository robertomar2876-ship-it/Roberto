package com.example.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.R
import com.example.ui.components.AcademicBrainLogo
import com.example.ui.components.AccessibilitySettingsDialog
import com.example.ui.components.PsicoTopAppBar
import com.example.ui.navigation.AppRoute
import com.example.ui.screens.AboutCreatorScreen
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.ArticlesListScreen
import com.example.ui.screens.ChatbotScreen
import com.example.ui.screens.ContactScreen
import com.example.ui.screens.DisorderDetailScreen
import com.example.ui.screens.DisordersLibraryScreen
import com.example.ui.screens.GalleriesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ResourcesScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.screens.VideosLibraryScreen
import com.example.ui.theme.PsicologiaParaTodosTheme
import com.example.ui.viewmodel.ChatViewModel
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

private data class NavMenuItem(
    val label: String,
    val icon: ImageVector,
    val route: AppRoute,
    val testTag: String
)

@Composable
fun PsicoApp(viewModel: MainViewModel) {
    val accessibility by viewModel.accessibilitySettings.collectAsStateWithLifecycle()
    val disorders by viewModel.disorders.collectAsStateWithLifecycle()
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val galleries by viewModel.galleries.collectAsStateWithLifecycle()
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    val resources by viewModel.resources.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val contactMessages by viewModel.contactMessages.collectAsStateWithLifecycle()
    val adminAuthState by viewModel.adminAuthState.collectAsStateWithLifecycle()
    val adminSession by viewModel.adminSession.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val storageLimits by viewModel.storageLimits.collectAsStateWithLifecycle()
    val storageUsage by viewModel.storageUsage.collectAsStateWithLifecycle()
    val chatViewModel: ChatViewModel = viewModel()
    val chatUiState by chatViewModel.uiState.collectAsStateWithLifecycle()

    val darkTheme = accessibility.darkThemePreference ?: isSystemInDarkTheme()
    var showAccessibilityDialog by remember { mutableStateOf(false) }

    PsicologiaParaTodosTheme(
        darkTheme = darkTheme,
        highContrast = accessibility.highContrast,
        fontScale = accessibility.fontScale
    ) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination

        fun navigateTopLevel(route: AppRoute) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        val primaryBottomItems = listOf(
            NavMenuItem("Inicio", Icons.Default.Home, AppRoute.Home, "nav_home"),
            NavMenuItem("Trastornos", Icons.Default.Psychology, AppRoute.Disorders, "nav_disorders"),
            NavMenuItem("Videos", Icons.Default.PlayCircle, AppRoute.Videos, "nav_videos"),
            NavMenuItem("Chatbot", Icons.Default.Forum, AppRoute.Chatbot, "nav_chatbot"),
            NavMenuItem("Admin", Icons.Default.AdminPanelSettings, AppRoute.Admin, "nav_admin")
        )

        val allDrawerItems = listOf(
            NavMenuItem("Inicio", Icons.Default.Home, AppRoute.Home, "drawer_home"),
            NavMenuItem("Chatbot psicoeducativo", Icons.Default.Forum, AppRoute.Chatbot, "drawer_chatbot"),
            NavMenuItem("Trastornos mentales (DSM-5-TR)", Icons.Default.Psychology, AppRoute.Disorders, "drawer_disorders"),
            NavMenuItem("Videos psicoeducativos", Icons.Default.PlayCircle, AppRoute.Videos, "drawer_videos"),
            NavMenuItem("Artículos científicos", Icons.Default.Article, AppRoute.Articles, "drawer_articles"),
            NavMenuItem("Recursos y PDF", Icons.Default.Description, AppRoute.Resources, "drawer_resources"),
            NavMenuItem("Galerías fotográficas", Icons.Default.Collections, AppRoute.Galleries, "drawer_galleries"),
            NavMenuItem("Buscador general", Icons.Default.Search, AppRoute.Search, "drawer_search"),
            NavMenuItem("Sobre el creador", Icons.Default.School, AppRoute.About, "drawer_about"),
            NavMenuItem("Contacto", Icons.Default.MailOutline, AppRoute.Contact, "drawer_contact"),
            NavMenuItem("Panel de administración", Icons.Default.AdminPanelSettings, AppRoute.Admin, "drawer_admin")
        )

        val isDetailRoute = currentDestination?.hasRoute<AppRoute.DisorderDetail>() == true ||
            currentDestination?.hasRoute<AppRoute.VideoPlayer>() == true ||
            currentDestination?.hasRoute<AppRoute.ArticleDetail>() == true

        val topBarTitle = when {
            currentDestination?.hasRoute<AppRoute.Disorders>() == true -> "Biblioteca DSM-5-TR"
            currentDestination?.hasRoute<AppRoute.DisorderDetail>() == true -> "Guía clínica educativa"
            currentDestination?.hasRoute<AppRoute.Videos>() == true -> "Videos Psicoeducativos"
            currentDestination?.hasRoute<AppRoute.VideoPlayer>() == true -> "Reproductor"
            currentDestination?.hasRoute<AppRoute.Articles>() == true -> "Artículos"
            currentDestination?.hasRoute<AppRoute.ArticleDetail>() == true -> "Lectura académica"
            currentDestination?.hasRoute<AppRoute.Resources>() == true -> "Recursos y PDF"
            currentDestination?.hasRoute<AppRoute.Galleries>() == true -> "Galerías"
            currentDestination?.hasRoute<AppRoute.Search>() == true -> "Buscador General"
            currentDestination?.hasRoute<AppRoute.Chatbot>() == true -> "Chatbot Psicoeducativo"
            currentDestination?.hasRoute<AppRoute.About>() == true -> "Sobre el Creador"
            currentDestination?.hasRoute<AppRoute.Contact>() == true -> "Contacto"
            currentDestination?.hasRoute<AppRoute.Admin>() == true -> "Administración"
            else -> stringResource(R.string.app_name)
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            AcademicBrainLogo(size = 40.dp)
                            Column {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = stringResource(R.string.app_subtitle),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        allDrawerItems.forEach { item ->
                            val selected = currentDestination?.hasRoute(item.route::class) == true
                            NavigationDrawerItem(
                                label = { Text(item.label) },
                                icon = { Icon(item.icon, contentDescription = null) },
                                selected = selected,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navigateTopLevel(item.route)
                                },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val useRail = maxWidth >= 600.dp

                Row(modifier = Modifier.fillMaxSize()) {
                    if (useRail && !isDetailRoute) {
                        NavigationRail(modifier = Modifier.fillMaxHeight()) {
                            Spacer(Modifier.height(12.dp))
                            allDrawerItems.take(6).plus(allDrawerItems.last()).forEach { item ->
                                val selected = currentDestination?.hasRoute(item.route::class) == true
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateTopLevel(item.route) },
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label.substringBefore(" ")) }
                                )
                            }
                        }
                    }

                    Scaffold(
                        modifier = Modifier.weight(1f),
                        topBar = {
                            PsicoTopAppBar(
                                title = topBarTitle,
                                canNavigateBack = isDetailRoute,
                                onNavigateBack = { navController.popBackStack() },
                                onOpenMenu = { scope.launch { drawerState.open() } },
                                onOpenSearch = { navigateTopLevel(AppRoute.Search) },
                                onOpenAccessibility = { showAccessibilityDialog = true }
                            )
                        },
                        bottomBar = {
                            if (!useRail && !isDetailRoute) {
                                NavigationBar {
                                    primaryBottomItems.forEach { item ->
                                        val selected = currentDestination?.hasRoute(item.route::class) == true
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = { navigateTopLevel(item.route) },
                                            icon = { Icon(item.icon, contentDescription = item.label) },
                                            label = { Text(item.label, maxLines = 1) },
                                            modifier = Modifier.testTag(item.testTag)
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = AppRoute.Home
                            ) {
                                composable<AppRoute.Home> {
                                    HomeScreen(
                                        videos = videos,
                                        articles = articles,
                                        resources = resources,
                                        galleries = galleries,
                                        disorderCount = disorders.size,
                                        onNavigateToVideos = { navigateTopLevel(AppRoute.Videos) },
                                        onNavigateToDisorders = { navigateTopLevel(AppRoute.Disorders) },
                                        onNavigateToArticles = { navigateTopLevel(AppRoute.Articles) },
                                        onNavigateToResources = { navigateTopLevel(AppRoute.Resources) },
                                        onNavigateToGalleries = { navigateTopLevel(AppRoute.Galleries) },
                                        onNavigateToAbout = { navigateTopLevel(AppRoute.About) },
                                        onNavigateToContact = { navigateTopLevel(AppRoute.Contact) },
                                        onNavigateToAdmin = { navigateTopLevel(AppRoute.Admin) },
                                        onNavigateToChatbot = { navigateTopLevel(AppRoute.Chatbot) },
                                        onOpenArticle = { id -> navController.navigate(AppRoute.ArticleDetail(id)) },
                                        onOpenVideo = { id -> navController.navigate(AppRoute.VideoPlayer(id)) }
                                    )
                                }

                                composable<AppRoute.Disorders> {
                                    DisordersLibraryScreen(
                                        disorders = disorders,
                                        onSelectDisorder = { id -> navController.navigate(AppRoute.DisorderDetail(id)) }
                                    )
                                }

                                composable<AppRoute.DisorderDetail> { entry ->
                                    val route = entry.toRoute<AppRoute.DisorderDetail>()
                                    DisorderDetailScreen(
                                        disorder = disorders.find { it.id == route.disorderId }
                                    )
                                }

                                composable<AppRoute.Videos> {
                                    VideosLibraryScreen(
                                        videos = videos,
                                        videoCategories = categories.filter { it.scope == "VIDEO" }.map { it.name },
                                        onSelectVideo = { id -> navController.navigate(AppRoute.VideoPlayer(id)) },
                                        onNavigateToAdminUpload = { navigateTopLevel(AppRoute.Admin) }
                                    )
                                }

                                composable<AppRoute.VideoPlayer> { entry ->
                                    val route = entry.toRoute<AppRoute.VideoPlayer>()
                                    VideoPlayerScreen(
                                        video = videos.find { it.id == route.videoId }
                                    )
                                }

                                composable<AppRoute.Articles> {
                                    ArticlesListScreen(
                                        articles = articles,
                                        categories = categories.filter { it.scope == "ARTICLE" }.map { it.name },
                                        onSelectArticle = { id -> navController.navigate(AppRoute.ArticleDetail(id)) }
                                    )
                                }

                                composable<AppRoute.ArticleDetail> { entry ->
                                    val route = entry.toRoute<AppRoute.ArticleDetail>()
                                    ArticleDetailScreen(
                                        article = articles.find { it.id == route.articleId }
                                    )
                                }

                                composable<AppRoute.Resources> {
                                    ResourcesScreen(
                                        resources = resources,
                                        categories = categories.filter { it.scope == "RESOURCE" }.map { it.name },
                                        storageManager = viewModel.repository.storageManager
                                    )
                                }

                                composable<AppRoute.Galleries> {
                                    GalleriesScreen(
                                        galleries = galleries,
                                        photos = photos,
                                        galleryCategories = categories.filter { it.scope == "GALLERY" }.map { it.name },
                                        onNavigateToAdminUpload = { navigateTopLevel(AppRoute.Admin) }
                                    )
                                }

                                composable<AppRoute.Search> {
                                    SearchScreen(
                                        disorders = disorders,
                                        videos = videos,
                                        articles = articles,
                                        photos = photos,
                                        resources = resources,
                                        allCategoryNames = categories.map { it.name } + disorders.map { it.categoryName },
                                        onOpenDisorder = { id -> navController.navigate(AppRoute.DisorderDetail(id)) },
                                        onOpenVideo = { id -> navController.navigate(AppRoute.VideoPlayer(id)) },
                                        onOpenArticle = { id -> navController.navigate(AppRoute.ArticleDetail(id)) },
                                        onOpenGalleries = { navigateTopLevel(AppRoute.Galleries) },
                                        onOpenResources = { navigateTopLevel(AppRoute.Resources) }
                                    )
                                }

                                composable<AppRoute.Chatbot> {
                                    ChatbotScreen(
                                        uiState = chatUiState,
                                        onSendMessage = chatViewModel::sendMessage,
                                        onResetConversation = chatViewModel::resetConversation,
                                        onClearError = chatViewModel::clearError
                                    )
                                }

                                composable<AppRoute.About> {
                                    AboutCreatorScreen()
                                }

                                composable<AppRoute.Contact> {
                                    ContactScreen(
                                        onSubmitMessage = viewModel::submitContactMessage
                                    )
                                }

                                composable<AppRoute.Admin> {
                                    AdminPanelScreen(
                                        authState = adminAuthState,
                                        sessionState = adminSession,
                                        uploadState = uploadState,
                                        storageLimits = storageLimits,
                                        storageUsage = storageUsage,
                                        videos = videos,
                                        galleries = galleries,
                                        photos = photos,
                                        articles = articles,
                                        resources = resources,
                                        categories = categories,
                                        contactMessages = contactMessages,
                                        onSetupAdmin = viewModel::setupAdminCredentials,
                                        onLoginAdmin = viewModel::loginAdmin,
                                        onLogoutAdmin = viewModel::logoutAdmin,
                                        onClearAuthError = viewModel::clearAuthError,
                                        onClearUploadFeedback = viewModel::clearUploadFeedback,
                                        onUploadVideo = viewModel::uploadVideo,
                                        onUpdateVideo = viewModel::updateVideo,
                                        onDeleteVideo = viewModel::deleteVideo,
                                        onCreateGallery = { t, d, c -> viewModel.createGallery(t, d, c) },
                                        onDeleteGallery = viewModel::deleteGallery,
                                        onUploadPhotos = viewModel::uploadPhotos,
                                        onUpdatePhoto = viewModel::updatePhoto,
                                        onDeletePhoto = viewModel::deletePhoto,
                                        onSaveArticle = viewModel::saveArticle,
                                        onDeleteArticle = viewModel::deleteArticle,
                                        onUploadResource = viewModel::uploadResourceDocument,
                                        onDeleteResource = viewModel::deleteResource,
                                        onAddCategory = viewModel::addCategory,
                                        onDeleteCategory = viewModel::deleteCategory,
                                        onUpdateStorageLimits = viewModel::updateStorageLimits,
                                        onMarkMessageRead = viewModel::markContactRead,
                                        onDeleteMessage = viewModel::deleteContactMessage
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAccessibilityDialog) {
            AccessibilitySettingsDialog(
                settings = accessibility,
                onFontScaleChange = viewModel::updateFontScale,
                onHighContrastChange = viewModel::updateHighContrast,
                onDarkThemeModeChange = viewModel::updateDarkThemeMode,
                onDismiss = { showAccessibilityDialog = false }
            )
        }
    }
}
