package com.example.studyassistant.studytracker.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.navigation.Route.SubjectScreen
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.core.presentation.util.SnackbarEvent
import com.example.studyassistant.studytracker.domain.model.Subject
import com.example.studyassistant.studytracker.domain.repository.SessionRepository
import com.example.studyassistant.studytracker.domain.repository.SubjectRepository
import com.example.studyassistant.studytracker.domain.repository.TaskRepository
import com.example.studyassistant.studytracker.presentation.mapper.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val navArgs = savedStateHandle.toRoute<SubjectScreen>()

    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(navArgs.subjectId),
        taskRepository.getCompletedTasksForSubject(navArgs.subjectId),
        sessionRepository.getRecentTenSessionsForSubject(navArgs.subjectId),
        sessionRepository.getTotalSessionDurationBySubject(navArgs.subjectId)
    ){ state, upcomingTasks, completedTasks, recentSessions, totalSessionDuration ->
        val goalStudyHours = state.goalStudyHours.toFloatOrNull() ?: 1f
            state.copy(
                upcomingTasks = upcomingTasks,
                completedTasks = completedTasks,
                recentSessions = recentSessions,
                studiedHours = totalSessionDuration.toHours(),
                progress = (state.studiedHours / goalStudyHours).coerceIn(0f, 1f)
            )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
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
            SubjectAction.UpdateSubject -> updateSubject()
            is SubjectAction.OnCancelSubjectChanges -> {
                _state.update {
                    it.copy(
                        subjectName = action.previousName,
                        goalStudyHours = action.previousGoalStudyHours,
                        subjectCardColors = action.previousColor
                    )
                }
            }
            SubjectAction.DeleteSubject -> deleteSubject()
            SubjectAction.DeleteSession -> { TODO() }
            is SubjectAction.OnDeleteSessionButtonClick -> { TODO() }
            is SubjectAction.OnTaskIsCompleteChange -> { TODO() }

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
                subjectRepository.upsertSubject(
                    subject = Subject(
                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
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
                if(currentSubjectId != null){
                    withContext(Dispatchers.IO) {
                        subjectRepository.deleteSubject(subjectId = currentSubjectId)
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

}