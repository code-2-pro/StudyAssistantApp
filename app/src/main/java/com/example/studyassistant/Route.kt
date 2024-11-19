package com.example.studyassistant

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object DashboardScreen: Route

    @Serializable
    data class SubjectScreen(val subjectId: Int): Route

    @Serializable
    data class TaskScreen(
        val taskId: Int?,
        val subjectId: Int?
    ): Route

    @Serializable
    data object SessionScreen: Route
}