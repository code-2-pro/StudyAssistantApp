package com.example.studyassistant.studytracker.data.util

import com.example.studyassistant.studytracker.domain.model.Task

fun sortTasks(tasks: List<Task>): List<Task>{
    return tasks.sortedWith(compareBy<Task> { it.dueDate }.thenByDescending { it.priority })
}