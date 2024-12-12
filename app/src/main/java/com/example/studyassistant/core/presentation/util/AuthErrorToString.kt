package com.example.studyassistant.core.presentation.util

import android.content.Context
import com.example.studyassistant.R
import com.example.studyassistant.core.domain.util.AuthError

fun AuthError.toString(context: Context): String{
    val resId = when(this){
        AuthError.EMAIL_IS_BLANK -> R.string.error_email_is_blank
        AuthError.PASSWORD_IS_BLANK -> R.string.error_password_is_blank
        AuthError.INVALID_EMAIL -> R.string.error_invalid_email
        AuthError.WEAK_PASSWORD -> R.string.error_weak_password
        AuthError.EMAIL_ALREADY_EXIST -> R.string.error_email_is_exist
        AuthError.INVALID_LOGIN_CREDENTIALS -> R.string.error_invalid_login_credentials
        AuthError.USER_NOT_FOUND -> R.string.error_user_not_found
        AuthError.NETWORK_ERROR -> R.string.error_network_error
        AuthError.TOO_MANY_REQUESTS -> R.string.error_too_many_request
        AuthError.UNKNOWN -> R.string.error_unknown
    }
    return context.getString(resId)
}