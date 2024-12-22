package com.example.studyassistant.core.presentation.util

import android.content.Context
import com.example.studyassistant.R
import com.example.studyassistant.core.domain.util.AuthError

fun AuthError.toString(context: Context): String {
    val resId = when (this) {
        AuthError.EMAIL_IS_BLANK -> R.string.error_email_is_blank
        AuthError.PASSWORD_IS_BLANK -> R.string.error_password_is_blank
        AuthError.INVALID_EMAIL -> R.string.error_invalid_email
        AuthError.WEAK_PASSWORD -> R.string.error_weak_password
        AuthError.EMAIL_ALREADY_EXIST -> R.string.error_email_is_exist
        AuthError.INVALID_LOGIN_CREDENTIALS -> R.string.error_invalid_login_credentials
        AuthError.USER_NOT_FOUND -> R.string.error_user_not_found
        AuthError.USER_DISABLED -> R.string.error_user_disabled
        AuthError.INVALID_PASSWORD -> R.string.error_invalid_password
        AuthError.ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL -> R.string.error_account_exists_with_different_credential
        AuthError.CUSTOM_TOKEN_MISMATCH -> R.string.error_custom_token_mismatch
        AuthError.INVALID_CUSTOM_TOKEN -> R.string.error_invalid_custom_token
        AuthError.CREDENTIAL_ALREADY_IN_USE -> R.string.error_credential_already_in_use
        AuthError.OPERATION_NOT_ALLOWED -> R.string.error_operation_not_allowed
        AuthError.MISSING_MFA_INFO -> R.string.error_missing_mfa_info
        AuthError.MISSING_MFA_PENDING_CREDENTIAL -> R.string.error_missing_mfa_pending_credential
        AuthError.INVALID_MFA_SESSION -> R.string.error_invalid_mfa_session
        AuthError.INVALID_MFA_PENDING_CREDENTIAL -> R.string.error_invalid_mfa_pending_credential
        AuthError.SECOND_FACTOR_EXISTS -> R.string.error_second_factor_exists
        AuthError.SECOND_FACTOR_LIMIT_EXCEEDED -> R.string.error_second_factor_limit_exceeded
        AuthError.NETWORK_ERROR -> R.string.error_network_error
        AuthError.TOO_MANY_REQUESTS -> R.string.error_too_many_request
        AuthError.REQUIRES_RECENT_LOGIN -> R.string.error_requires_recent_login
        AuthError.UNKNOWN -> R.string.error_unknown
    }
    return context.getString(resId)
}