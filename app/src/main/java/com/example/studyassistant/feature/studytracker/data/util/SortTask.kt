package com.example.studyassistant.feature.studytracker.data.util

import com.example.studyassistant.feature.studytracker.domain.model.Task

fun sortTasks(tasks: List<Task>): List<Task>{
    return tasks.sortedWith(compareBy<Task> { it.dueDate }.thenByDescending { it.priority })
}