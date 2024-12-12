package com.example.studyassistant.feature.authentication.presentation

import com.example.studyassistant.feature.authentication.domain.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val isConnected: Boolean = false,
    val hasLocalData: Boolean = false,
    val currentUser: User? = null
)
