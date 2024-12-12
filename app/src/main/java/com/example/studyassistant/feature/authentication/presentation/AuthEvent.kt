package com.example.studyassistant.feature.authentication.presentation

import com.example.studyassistant.core.domain.util.AuthError
import com.example.studyassistant.core.domain.util.RemoteDbError

sealed interface AuthEvent{
    data class AuthenticationError(val error: AuthError): AuthEvent
    data class SyncError(val error: RemoteDbError): AuthEvent
    data class SyncChange(val changedMap: Map<String, Int>): AuthEvent
}


