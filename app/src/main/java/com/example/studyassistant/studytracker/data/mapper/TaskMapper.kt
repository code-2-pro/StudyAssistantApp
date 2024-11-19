package com.example.studyassistant.studytracker.data.mapper

import com.example.studyassistant.studytracker.data.local.entity.TaskEntity
import com.example.studyassistant.studytracker.domain.model.Task

fun TaskEntity.toTask(): Task{
    return Task(
        title = title,
        description = description,
        dueDate = dueDate,
        priority = priority,
        relatedToSubject = relatedToSubject,
        isComplete = isComplete,
        taskSubjectId = taskSubjectId,
        taskId = taskId
    )
}

fun Task.toTaskEntity(): TaskEntity{
    return TaskEntity(
        title = title,
        description = description,
        dueDate = dueDate,
        priority = priority,
        relatedToSubject = relatedToSubject,
        isComplete = isComplete,
        taskSubjectId = taskSubjectId,
        taskId = taskId
    )
}
