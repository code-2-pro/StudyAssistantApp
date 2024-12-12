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
    data object FlashcardScreen: Route

    // Setting Graph
    @Serializable
    data object Setting: Route

    @Serializable
    data object MainSetting: Route
}