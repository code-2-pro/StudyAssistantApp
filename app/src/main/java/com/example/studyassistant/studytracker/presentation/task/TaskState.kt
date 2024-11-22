package com.example.studyassistant.studytracker.presentation.task

import com.example.studyassistant.studytracker.domain.model.Subject
import com.example.studyassistant.studytracker.presentation.util.Priority

data class TaskState(
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val isTaskComplete: Boolean = false,
    val priority: Priority = Priority.LOW,
    val subjects: List<Subject> = emptyList(),
    val relatedToSubject: String? = null,
    val subjectId: Int? = null,
    val currentTaskId: Int? = null
)
