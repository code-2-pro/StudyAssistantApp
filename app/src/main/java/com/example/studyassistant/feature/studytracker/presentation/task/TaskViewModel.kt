package com.example.studyassistant.feature.studytracker.presentation.task

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route.TaskScreen
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.core.presentation.util.SnackbarEvent
import com.example.studyassistant.feature.studytracker.domain.model.Task
import com.example.studyassistant.feature.studytracker.domain.repository.SubjectRepository
import com.example.studyassistant.feature.studytracker.domain.repository.TaskRepository
import com.example.studyassistant.feature.studytracker.presentation.util.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val navArgs = savedStateHandle.toRoute<TaskScreen>()

    private val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects()
    ){ state, subjects ->
        state.copy(subjects = subjects)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TaskState()
    )

    init {
        fetchTask()
        fetchSubject()
    }

    fun onAction(action: TaskAction){
        when(action){
            is TaskAction.OnTitleChange -> {
                _state.update {
                    it.copy(title = action.title)
                }
            }
            is TaskAction.OnDescriptionChange -> {
                _state.update {
                    it.copy(description = action.description)
                }
            }
            is TaskAction.OnDateChange -> {
                _state.update {
                    it.copy(dueDate = action.millis)
                }
            }
            is TaskAction.OnPriorityChange -> {
                _state.update {
                    it.copy(priority = action.priority)
                }
            }
            TaskAction.OnIsCompleteChange -> {
                _state.update {
                    it.copy(isTaskComplete = !_state.value.isTaskComplete)
                }
            }
            is TaskAction.OnRelatedSubjectSelect -> {
                _state.update {
                    it.copy(
                        relatedToSubject = action.subject.name,
                        subjectId = action.subject.subjectId
                    )
                }
            }
            TaskAction.SaveTask -> saveTask()
            TaskAction.DeleteTask -> deleteTask()
        }
    }

    private fun deleteTask() {
        viewModelScope.launch{
            try {
                val currentTaskId = state.value.currentTaskId
                if(currentTaskId.isNotBlank()){
                    withContext(Dispatchers.IO) {
                        taskRepository.deleteTask(taskId = currentTaskId)
                    }
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "Task deleted successfully."
                        )
                    )
                    navigator.navigateUp()
                }else{
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "No Task to delete."
                        )
                    )
                }
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't delete task. ${e.message}",
                        duration =  SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun saveTask() {
        viewModelScope.launch{
            val state = _state.value
            if(state.subjectId.isBlank() || state.relatedToSubject == null){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Please select subject related to the task.",
                        duration = SnackbarDuration.Long
                    )
                )
                return@launch
            }
            try {
                taskRepository.upsertTask(
                    task = Task(
                        title = state.title,
                        description = state.description,
                        dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                        priority = state.priority.value,
                        relatedToSubject = state.relatedToSubject,
                        isComplete = state.isTaskComplete,
                        taskSubjectId = state.subjectId,
                        taskId = if(state.currentTaskId.isBlank()) {
                            UUID.randomUUID().toString()
                        }else state.currentTaskId
                    )
                )
                SnackbarController.sendEvent(
                    event = SnackbarEvent(message = "Task Saved Successfully.")
                )
                navigator.navigateUp()
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't save task. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetchTask(){
        viewModelScope.launch{
            navArgs.taskId?.let { id ->
                taskRepository.getTaskById(id).collect{task ->
                    task?.let {
                        _state.update {
                            it.copy(
                                title = task.title,
                                description = task.description,
                                dueDate = task.dueDate,
                                isTaskComplete = task.isComplete,
                                relatedToSubject = task.relatedToSubject,
                                priority = Priority.fromInt(task.priority),
                                subjectId = task.taskSubjectId,
                                currentTaskId = task.taskId
                            )
                        }
                    }
                }
            }
        }
    }

    private fun fetchSubject(){
        viewModelScope.launch{
            navArgs.subjectId?.let { id ->
                subjectRepository.getSubjectById(id).collect{ subject ->
                    subject?.let {
                        _state.update {
                            it.copy(
                                subjectId = subject.subjectId,
                                relatedToSubject = subject.name
                            )
                        }
                    }
                }
            }
        }
    }


}