package com.example.studyassistant.studytracker.presentation.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyassistant.core.presentation.util.SnackbarEvent
import com.example.studyassistant.studytracker.domain.model.Session
import com.example.studyassistant.studytracker.domain.model.Subject
import com.example.studyassistant.studytracker.domain.model.Task
import com.example.studyassistant.studytracker.domain.repository.SessionRepository
import com.example.studyassistant.studytracker.domain.repository.SubjectRepository
import com.example.studyassistant.studytracker.domain.repository.TaskRepository
import com.example.studyassistant.studytracker.presentation.mapper.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository
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

    private val _snackbarEvent = Channel<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

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
            DashboardAction.DeleteSession -> TODO()
            is DashboardAction.OnTaskIsCompleteChange -> TODO()
        }
    }

    private fun saveSubject() {
        viewModelScope.launch{
            try{
                subjectRepository.upsertSubject(
                    subject = Subject(
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _state.update {
                    it.copy(
                        subjectName = "",
                        goalStudyHours = ""
                    )
                }
                _snackbarEvent.send(
                     SnackbarEvent.ShowSnackBar( message = "Subject saved successfully.")
                )
            }catch (e: Exception){
                _snackbarEvent.send(
                    SnackbarEvent.ShowSnackBar(
                        message =  "Couldn't save subject. ${e.message}",
                        duration =  SnackbarDuration.Long
                    )
                )
            }

        }
    }

}