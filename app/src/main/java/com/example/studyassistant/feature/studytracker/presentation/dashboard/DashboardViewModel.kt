package com.example.studyassistant.feature.studytracker.presentation.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.core.presentation.util.SnackbarEvent
import com.example.studyassistant.feature.authentication.domain.repository.AuthRepository
import com.example.studyassistant.feature.studytracker.domain.model.Session
import com.example.studyassistant.feature.studytracker.domain.model.Subject
import com.example.studyassistant.feature.studytracker.domain.model.Task
import com.example.studyassistant.feature.studytracker.domain.repository.SessionRepository
import com.example.studyassistant.feature.studytracker.domain.repository.SubjectRepository
import com.example.studyassistant.feature.studytracker.domain.repository.TaskRepository
import com.example.studyassistant.feature.studytracker.presentation.mapper.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository,
): ViewModel() {

    val _state = MutableStateFlow(DashboardState())
    val state = combine(
        _state,
        subjectRepository.getTotalSubjectCount(),
        subjectRepository.getTotalGoalHours(),
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionsDuration()
    ){ state, subjectCount, goalHours, subjects, totalSessionDuration ->
        state.copy(
            totalSubjectCount = subjectCount,
            totalGoalStudyHours = goalHours,
            subjects = subjects,
            totalStudiedHours = totalSessionDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    val tasks : StateFlow<List<Task>> = taskRepository.getAllUpcomingTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = emptyList()
        )

    val recentSessions : StateFlow<List<Session>> = sessionRepository.getRecentFiveSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
    )

    val isOnline: StateFlow<Boolean> = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope, // Use the ViewModel's coroutine scope
            started = SharingStarted.Eagerly, // Start eagerly collecting the flow
            initialValue = false
        )


    fun onAction(action: DashboardAction){
        when(action){
            is DashboardAction.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = action.name)
                }
            }
            is DashboardAction.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = action.hours)
                }
            }
            is DashboardAction.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = action.colors)
                }
            }
            is DashboardAction.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = action.session)
                }
            }
            DashboardAction.SaveSubject -> saveSubject()
            DashboardAction.DeleteSession -> deleteSession()
            is DashboardAction.OnTaskIsCompleteChange -> {
                updateTask(action.task)
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch{
            try{
                val task = task.copy(isComplete = !task.isComplete)
                taskRepository.upsertTask(task)
                val currentUser = authRepository.getCurrentUser()
                if(isOnline.value && currentUser != null){
                    taskRepository.upsertTaskOnRemote(
                        task = task,
                        userId = currentUser.userId.toString()
                    )
                }
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Saved in completed tasks."
                    )
                )
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't update task. ${e.message}",
                        duration =  SnackbarDuration.Long
                    )
                )
            }

        }
    }

    private fun saveSubject() {
        viewModelScope.launch{
            try{
                val subject = Subject(
                    subjectId = UUID.randomUUID().toString(),
                    name = state.value.subjectName,
                    goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                    colors = state.value.subjectCardColors.map { it.toArgb() }
                )
                subjectRepository.upsertSubject(subject)
                val currentUser = authRepository.getCurrentUser()
                if(isOnline.value && currentUser != null){
                    subjectRepository.upsertSubjectOnRemote(
                        subject = subject,
                        userId = currentUser.userId.toString()
                    )
                }
                _state.update {
                    it.copy(
                        subjectName = "",
                        goalStudyHours = ""
                    )
                }
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Subject saved successfully."
                    )
                )
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't save subject. ${e.message}",
                        duration =  SnackbarDuration.Long
                    )
                )
            }

        }
    }

    private fun deleteSession() {
        viewModelScope.launch{
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                    val currentUser = authRepository.getCurrentUser()
                    if(isOnline.value && currentUser != null){
                        sessionRepository.deleteSessionOnRemote(
                            session =  it,
                            userId = currentUser.userId.toString()
                        )
                    }
                }
                SnackbarController.sendEvent(
                    event = SnackbarEvent(message = "Session deleted successfully.")
                )
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't delete session. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

}