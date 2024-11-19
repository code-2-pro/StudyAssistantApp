package com.example.studyassistant.studytracker.presentation.subject

import androidx.compose.ui.graphics.Color
import com.example.studyassistant.studytracker.domain.model.Session
import com.example.studyassistant.studytracker.domain.model.Task

sealed interface SubjectAction {
    object UpdateSubject: SubjectAction
    object DeleteSubject: SubjectAction
    object DeleteSession: SubjectAction
    data class OnTaskIsCompleteChange(val task: Task): SubjectAction
    data class OnSubjectCardColorChange(val color: List<Color>): SubjectAction
    data class OnSubjectNameChange(val name: String): SubjectAction
    data class OnGoalStudyHoursChange(val hour: String): SubjectAction
    data class OnDeleteSessionButtonClick(val session: Session): SubjectAction
}