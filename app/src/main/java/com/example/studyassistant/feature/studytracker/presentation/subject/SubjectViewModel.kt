package com.example.studyassistant.feature.studytracker.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.core.presentation.util.SnackbarEvent
import com.example.studyassistant.feature.authentication.domain.repository.AuthRepository
import com.example.studyassistant.feature.studytracker.domain.model.Subject
import com.example.studyassistant.feature.studytracker.domain.model.Task
import com.example.studyassistant.feature.studytracker.domain.repository.SessionRepository
import com.example.studyassistant.feature.studytracker.domain.repository.SubjectRepository
import com.example.studyassistant.feature.studytracker.domain.repository.TaskRepository
import com.example.studyassistant.feature.studytracker.presentation.mapper.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository,
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val navArgs = savedStateHandle.toRoute<Route.SubjectScreen>()

    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(navArgs.subjectId),
        taskRepository.getCompletedTasksForSubject(navArgs.subjectId),
        sessionRepository.getRecentTenSessionsForSubject(navArgs.subjectId),
        sessionRepository.getTotalSessionDurationBySubject(navArgs.subjectId)
    ){ state, upcomingTasks, completedTasks, recentSessions, totalSessionDuration ->
            state.copy(
                upcomingTasks = upcomingTasks,
                completedTasks = completedTasks,
                recentSessions = recentSessions,
                studiedHours = totalSessionDuration.toHours(),
            )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
    )

    val isOnline: StateFlow<Boolean> = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        fetchSubject()
    }

    fun onAction(action: SubjectAction){
        when(action){
            is SubjectAction.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = action.name)
                }
            }
            is SubjectAction.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = action.hour)
                }
            }
            is SubjectAction.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = action.color)
                }
            }
            is SubjectAction.OnCancelSubjectChanges -> {
                _state.update {
                    it.copy(
                        subjectName = action.previousName,
                        goalStudyHours = action.previousGoalStudyHours,
                        subjectCardColors = action.previousColor
                    )
                }
            }
            SubjectAction.UpdateSubject -> updateSubject()
            SubjectAction.DeleteSubject -> deleteSubject()
            is SubjectAction.OnTaskIsCompleteChange -> {
                updateTask(action.task)
            }
            is SubjectAction.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = action.session)
                }
            }
            SubjectAction.DeleteSession ->  deleteSession()
            SubjectAction.UpdateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours / goalStudyHours).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    private fun fetchSubject(){
        viewModelScope.launch{
            subjectRepository.getSubjectById(navArgs.subjectId).collect { subject ->
                subject?.let {
                    _state.update {
                        it.copy(
                            subjectName = subject.name.toString(),
                            goalStudyHours = subject.goalHours.toString(),
                            subjectCardColors = subject.colors.map { Color(it) },
                            currentSubjectId = subject.subjectId
                        )
                    }
                }
            }
        }
    }

    private fun updateSubject() {
        viewModelScope.launch{
            try {
                val subject = Subject(
                    subjectId = state.value.currentSubjectId,
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
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Subject updated successfully."
                    )
                )
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't update subject. ${e.message}",
                        duration =  SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun deleteSubject(){
        viewModelScope.launch{
            try {
                val currentSubjectId = state.value.currentSubjectId
                if(currentSubjectId.isNotBlank()){
                    withContext(Dispatchers.IO) {
                        subjectRepository.deleteSubject(subjectId = currentSubjectId)
                        val currentUser = authRepository.getCurrentUser()
                        if(isOnline.value && currentUser != null){
                            subjectRepository.deleteSubjectOnRemote(
                                subjectId = currentSubjectId,
                                userId = currentUser.userId.toString()
                            )
                        }
                    }
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "Subject deleted successfully."
                        )
                    )
                    navigator.navigateUp()
                }else{
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "No Subject to delete."
                        )
                    )
                }
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't delete subject. ${e.message}",
                        duration =  SnackbarDuration.Long
                    )
                )
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
                if(task.isComplete){
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "Saved in upcoming tasks."
                        )
                    )
                }else{
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = "Saved in completed tasks."
                        )
                    )
                }
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