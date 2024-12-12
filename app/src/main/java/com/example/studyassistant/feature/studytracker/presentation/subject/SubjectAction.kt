package com.example.studyassistant.feature.studytracker.presentation.subject

import androidx.compose.ui.graphics.Color
import com.example.studyassistant.feature.studytracker.domain.model.Session
import com.example.studyassistant.feature.studytracker.domain.model.Task

sealed interface SubjectAction {
    object UpdateSubject: SubjectAction
    object DeleteSubject: SubjectAction
    object DeleteSession: SubjectAction
    object UpdateProgress : SubjectAction
    data class OnCancelSubjectChanges(
        val previousName: String,
        val previousGoalStudyHours: String,
        val previousColor: List<Color>
    ): SubjectAction
    data class OnTaskIsCompleteChange(val task: Task): SubjectAction
    data class OnSubjectCardColorChange(val color: List<Color>): SubjectAction
    data class OnSubjectNameChange(val name: String): SubjectAction
    data class OnGoalStudyHoursChange(val hour: String): SubjectAction
    data class OnDeleteSessionButtonClick(val session: Session): SubjectAction
}