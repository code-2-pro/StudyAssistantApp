package com.example.studyassistant.feature.authentication.presentation

sealed interface AuthAction {
    data class Login(
        val email: String,
        val password: String
    ): AuthAction
    data class Register(
        val email: String,
        val password: String,
        val displayName: String
    ): AuthAction
    data class UpdateUserInfo(
        val currentPassword: String,
        val newEmail: String,
        val newPassword: String,
        val newDisplayName: String
    ): AuthAction
    object LogoutKeepLocalData: AuthAction
    object LogoutRemoveLocalData: AuthAction
    object GoToLoginPage: AuthAction
    object GoToRegisterPage: AuthAction
    object SendDataToRemote: AuthAction
    object GetDataFromRemote: AuthAction
    object DismissSync: AuthAction
    object UseNoAccount: AuthAction
}