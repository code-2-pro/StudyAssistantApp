package com.example.studyassistant.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {

    // Authentication Graph
    @Serializable
    data object Authentication: Route

    @Serializable
    data object LoginScreen: Route

    @Serializable
    data object RegisterScreen: Route

    // StudyTracker Graph
    @Serializable
    data object StudyTracker: Route

    @Serializable
    data object DashboardScreen: Route

    @Serializable
    data class SubjectScreen(val subjectId: String): Route

    @Serializable
    data class TaskScreen(
        val taskId: String?,
        val subjectId: String?
    ): Route

    @Serializable
    data object SessionScreen: Route

    // Flashcard Graph
    @Serializable
    data object Flashcard: Route

    @Serializable
    data object CategoryScreen: Route

    @Serializable
    data class CategoryDetailScreen(
        val categoryId: String
    ): Route

    @Serializable
    data class FlashcardScreen(
        val flashcardId: String?,
        val categoryId: String?
    ): Route

    @Serializable
    data class FlashcardDisplayScreen(
        val categoryId: String
    ): Route

    // Utility Graph
    @Serializable
    data object Utility: Route

    @Serializable
    data object UtilityScreen: Route

    @Serializable
    data object AssistantScreen: Route

    @Serializable
    data object DocumentAnalyzerScreen: Route

    // Setting Graph
    @Serializable
    data object Setting: Route

    @Serializable
    data object MainSettingScreen: Route

    @Serializable
    data object AccountScreen: Route
}