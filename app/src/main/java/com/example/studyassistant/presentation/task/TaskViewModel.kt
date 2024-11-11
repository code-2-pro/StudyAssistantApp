package com.example.studyassistant.presentation.task

import androidx.lifecycle.ViewModel
import com.example.studyassistant.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {
}