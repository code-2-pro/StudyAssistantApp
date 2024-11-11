package com.example.studyassistant.presentation.task

import androidx.lifecycle.ViewModel
import com.example.studyassistant.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class TaskViewModel(
    private val taskRepository: TaskRepository
): ViewModel() {
}