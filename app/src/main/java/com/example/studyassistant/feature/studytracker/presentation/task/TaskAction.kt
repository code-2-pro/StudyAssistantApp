package com.example.studyassistant.feature.studytracker.presentation.task

import com.example.studyassistant.feature.studytracker.domain.model.Subject
import com.example.studyassistant.feature.studytracker.presentation.util.Priority

sealed interface TaskAction {

    data class OnTitleChange(val title: String): TaskAction
    data class OnDescriptionChange(val description: String): TaskAction
    data class OnDateChange(val millis: Long?): TaskAction
    data class OnPriorityChange(val priority: Priority): TaskAction
    data class OnRelatedSubjectSelect(val subject: Subject): TaskAction
    object OnIsCompleteChange: TaskAction
    object SaveTask: TaskAction
    object DeleteTask: TaskAction
}