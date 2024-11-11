package com.example.studyassistant.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.studyassistant.domain.model.Session
import com.example.studyassistant.domain.model.Subject

data class DashboardState(
    val totalSubjectCount: Int = 0,
    val totalStudiedHours: Float = 0f,
    val totalGoalStudyHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors: List<Color> = Subject.subjectCardColors.first(),
    val session: Session? = null

)
