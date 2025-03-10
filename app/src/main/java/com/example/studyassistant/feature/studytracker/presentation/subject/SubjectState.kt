package com.example.studyassistant.feature.studytracker.presentation.subject

import androidx.compose.ui.graphics.Color
import com.example.studyassistant.feature.studytracker.domain.model.Session
import com.example.studyassistant.feature.studytracker.domain.model.Subject
import com.example.studyassistant.feature.studytracker.domain.model.Task

data class SubjectState(
    val currentSubjectId: String = "",
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors: List<Color> = Subject.subjectCardColors.last(),
    val studiedHours: Float = 0f,
    val progress: Float = 0f,
    val recentSessions: List<Session> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: Session? = null
)
