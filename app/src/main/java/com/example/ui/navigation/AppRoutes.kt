package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
    @Serializable data object Home : AppRoute
    @Serializable data object Disorders : AppRoute
    @Serializable data class DisorderDetail(val disorderId: Int) : AppRoute
    @Serializable data object Videos : AppRoute
    @Serializable data class VideoPlayer(val videoId: Int) : AppRoute
    @Serializable data object Articles : AppRoute
    @Serializable data class ArticleDetail(val articleId: Int) : AppRoute
    @Serializable data object Resources : AppRoute
    @Serializable data object Galleries : AppRoute
    @Serializable data object Search : AppRoute
    @Serializable data object Chatbot : AppRoute
    @Serializable data object About : AppRoute
    @Serializable data object Contact : AppRoute
    @Serializable data object Admin : AppRoute
}
