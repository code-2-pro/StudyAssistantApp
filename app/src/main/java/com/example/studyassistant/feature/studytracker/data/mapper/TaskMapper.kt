package com.example.studyassistant.feature.studytracker.data.mapper

import com.example.studyassistant.feature.authentication.data.dto.RemoteTask
import com.example.studyassistant.feature.studytracker.data.local.entity.TaskEntity
import com.example.studyassistant.feature.studytracker.domain.model.Task

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

fun Task.toRemoteTask(userId: String): RemoteTask{
    return RemoteTask(
        userId = userId,
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

fun RemoteTask.toTask(): Task{
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
