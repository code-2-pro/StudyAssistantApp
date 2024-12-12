package com.example.studyassistant.feature.authentication.data.util

import com.example.studyassistant.core.domain.util.AuthError
import com.example.studyassistant.core.domain.util.Result
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

// Utility function to map exceptions to AuthError
fun mapAuthExceptionToError(exception: Exception): Result.Error<AuthError> {
    return when (exception) {
        // Weak password during sign-up
        is FirebaseAuthWeakPasswordException -> Result.Error(AuthError.WEAK_PASSWORD)

        // Invalid credentials, such as email or password issues
        is FirebaseAuthInvalidCredentialsException -> {
            when (exception.errorCode) {
                "ERROR_INVALID_EMAIL" -> Result.Error(AuthError.INVALID_EMAIL)
                else -> Result.Error(AuthError.UNKNOWN)
            }
        }

        // Invalid user or user-related errors
        is FirebaseAuthInvalidUserException -> {
            when (exception.errorCode) {
                "ERROR_USER_NOT_FOUND" -> Result.Error(AuthError.USER_NOT_FOUND)
                else -> Result.Error(AuthError.UNKNOWN)
            }
        }

        // Email already exists during sign-up
        is FirebaseAuthUserCollisionException -> {
            when (exception.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> Result.Error(AuthError.EMAIL_ALREADY_EXIST)
                else -> Result.Error(AuthError.UNKNOWN)
            }
        }

        // Firebase-specific exceptions
        is FirebaseAuthException -> {
            when (exception.errorCode) {
                "ERROR_NETWORK_REQUEST_FAILED" -> Result.Error(AuthError.NETWORK_ERROR)
                "ERROR_TOO_MANY_REQUESTS" -> Result.Error(AuthError.TOO_MANY_REQUESTS)
                else -> Result.Error(AuthError.UNKNOWN)

            }
        }

        is FirebaseNetworkException -> {
            Result.Error(AuthError.NETWORK_ERROR)
        }

        // Handle FirebaseException for internal errors like INVALID_LOGIN_CREDENTIALS
        is FirebaseException -> {
            if (exception.message?.contains("INVALID_LOGIN_CREDENTIALS") == true) {
                Result.Error(AuthError.INVALID_LOGIN_CREDENTIALS)
            } else {
                Result.Error(AuthError.UNKNOWN)
            }
        }
        // Fallback for unexpected exceptions
        else -> Result.Error(AuthError.UNKNOWN)
    }
}