package com.example.studyassistant.feature.studytracker.presentation.session

import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.core.presentation.util.SnackbarEvent
import com.example.studyassistant.feature.authentication.domain.repository.AuthRepository
import com.example.studyassistant.feature.studytracker.domain.model.Session
import com.example.studyassistant.feature.studytracker.domain.repository.SessionRepository
import com.example.studyassistant.feature.studytracker.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    connectivityObserver: ConnectivityObserver,
    subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    private val _state = MutableStateFlow(SessionState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects(),
        sessionRepository.getAllSessions()
    ){ state, subjects, sessions ->
        state.copy(
            subjects = subjects,
            sessions = sessions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SessionState()
    )

    val isOnline: StateFlow<Boolean> = connectivityObserver.isConnected
        .stateIn(
            scope = viewModelScope, // Use the ViewModel's coroutine scope
            started = SharingStarted.Eagerly, // Start eagerly collecting the flow
            initialValue = false
        )

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeAt(0)
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    fun onAction(action: SessionAction){
        when(action){
            is SessionAction.SaveSession -> insertSession(action.duration)
            is SessionAction.OnRelatedSubjectChange -> {
                _state.update {
                    it.copy(
                        relatedToSubject = action.subject.name,
                        subjectId =  action.subject.subjectId
                    )
                }
            }
            is SessionAction.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = action.session)
                }
            }
            SessionAction.DeleteSession -> deleteSession()
            SessionAction.NotifyToUpdateSubject -> notifyToUpdateSubject()
            is SessionAction.UpdateSubjectIdAndRelatedSubject -> {
                _state.update {
                    it.copy(
                        relatedToSubject = action.relatedToSubject,
                        subjectId = action.subjectId.toString()
                    )
                }
            }
        }
    }

    private fun notifyToUpdateSubject() {
        viewModelScope.launch{
            if(state.value.subjectId.isBlank() || state.value.relatedToSubject == null){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Please select subject related to the session."
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

    private fun insertSession(duration: Long) {
        viewModelScope.launch{
            if(duration < 36){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Single session can not be less than 36 seconds."
                    )
                )
                return@launch
            }
            try {
                val session = Session(
                    sessionSubjectId = state.value.subjectId,
                    relatedToSubject = state.value.relatedToSubject ?: "",
                    date = Instant.now().toEpochMilli(),
                    duration = duration,
                    sessionId = UUID.randomUUID().toString()
                )
                sessionRepository.insertSession(session)
                val currentUser = authRepository.getCurrentUser()
                if(isOnline.value && currentUser != null){
                    sessionRepository.insertSessionOnRemote(
                        session = session,
                        userId = currentUser.userId.toString()
                    )
                }
                SnackbarController.sendEvent(
                    event = SnackbarEvent(message = "Session saved successfully.")
                )
            }catch (e: Exception){
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Couldn't save session. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }

    }


}