package com.example.studyassistant.feature.authentication.presentation

sealed interface AuthAction {
    data class OnLoginClick(
        val email: String,
        val password: String
    ): AuthAction
    data class OnRegisterClick(
        val email: String,
        val password: String,
        val displayName: String
    ): AuthAction
    object OnLogoutKeepLocalDataClick: AuthAction
    object OnLogoutRemoveLocalDataClick: AuthAction
    object OnToLoginPageClick: AuthAction
    object OnToRegisterPageClick: AuthAction
    object OnSendDataToRemoteClick: AuthAction
    object OnGetDataFromRemoteClick: AuthAction
    object OnUseNoAccountClick: AuthAction
}