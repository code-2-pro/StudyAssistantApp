package com.example.studyassistant.feature.authentication.data.util

import com.example.studyassistant.core.domain.util.AuthError
import com.example.studyassistant.core.domain.util.Result
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

// Utility function to map exceptions to AuthError
fun mapAuthExceptionToError(exception: Exception): Result.Error<AuthError> {
    // Centralized error code to AuthError mapping
    val errorCodeToAuthError = mapOf(
        // Weak password during sign-up
        "ERROR_WEAK_PASSWORD" to AuthError.WEAK_PASSWORD,

        // Invalid credentials errors
        "ERROR_INVALID_EMAIL" to AuthError.INVALID_EMAIL,
        "ERROR_WRONG_PASSWORD" to AuthError.INVALID_PASSWORD,

        // User-related errors
        "ERROR_USER_DISABLED" to AuthError.USER_DISABLED,
        "ERROR_USER_NOT_FOUND" to AuthError.USER_NOT_FOUND,

        // User collision errors
        "ERROR_EMAIL_ALREADY_IN_USE" to AuthError.EMAIL_ALREADY_EXIST,
        "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" to AuthError.ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL,

        // Recent login required
        "ERROR_REQUIRES_RECENT_LOGIN" to AuthError.REQUIRES_RECENT_LOGIN,

        // FirebaseAuthException errors
        "ERROR_CUSTOM_TOKEN_MISMATCH" to AuthError.CUSTOM_TOKEN_MISMATCH,
        "ERROR_INVALID_CUSTOM_TOKEN" to AuthError.INVALID_CUSTOM_TOKEN,
        "ERROR_CREDENTIAL_ALREADY_IN_USE" to AuthError.CREDENTIAL_ALREADY_IN_USE,
        "ERROR_OPERATION_NOT_ALLOWED" to AuthError.OPERATION_NOT_ALLOWED,
        "ERROR_TOO_MANY_REQUESTS" to AuthError.TOO_MANY_REQUESTS,
        "ERROR_NETWORK_REQUEST_FAILED" to AuthError.NETWORK_ERROR,

        // Multi-factor authentication errors
        "ERROR_MISSING_MFA_INFO" to AuthError.MISSING_MFA_INFO,
        "ERROR_MISSING_MFA_PENDING_CREDENTIAL" to AuthError.MISSING_MFA_PENDING_CREDENTIAL,
        "ERROR_INVALID_MFA_SESSION" to AuthError.INVALID_MFA_SESSION,
        "ERROR_INVALID_MFA_PENDING_CREDENTIAL" to AuthError.INVALID_MFA_PENDING_CREDENTIAL,
        "ERROR_SECOND_FACTOR_EXISTS" to AuthError.SECOND_FACTOR_EXISTS,
        "ERROR_SECOND_FACTOR_LIMIT_EXCEEDED" to AuthError.SECOND_FACTOR_LIMIT_EXCEEDED
    )

    // Retrieve the error code if applicable and map it to the AuthError
    val authError = when (exception) {
        is FirebaseAuthWeakPasswordException -> AuthError.WEAK_PASSWORD
        is FirebaseAuthException -> errorCodeToAuthError[exception.errorCode] ?: AuthError.UNKNOWN
        is FirebaseNetworkException -> AuthError.NETWORK_ERROR
        is FirebaseException -> if (exception.message?.contains("INVALID_LOGIN_CREDENTIALS") == true) {
            AuthError.INVALID_LOGIN_CREDENTIALS
        } else {
            AuthError.UNKNOWN
        }
        else -> AuthError.UNKNOWN
    }

    // Return the wrapped error
    return Result.Error(authError)
}

