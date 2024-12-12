package com.example.studyassistant.feature.studytracker.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.studyassistant.feature.studytracker.domain.model.Session
import com.example.studyassistant.feature.studytracker.domain.model.Task

sealed interface DashboardAction {

    object SaveSubject: DashboardAction
    object DeleteSession: DashboardAction
    data class OnDeleteSessionButtonClick(val session: Session): DashboardAction
    data class OnTaskIsCompleteChange(val task: Task): DashboardAction
    data class OnSubjectCardColorChange(val colors: List<Color>): DashboardAction
    data class OnSubjectNameChange(val name: String): DashboardAction
    data class OnGoalStudyHoursChange(val hours: String): DashboardAction
}