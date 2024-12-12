package com.example.studyassistant.core.domain.util

enum class AuthError: Error {
    EMAIL_IS_BLANK,
    PASSWORD_IS_BLANK,
    WEAK_PASSWORD,
    INVALID_EMAIL,
    INVALID_LOGIN_CREDENTIALS,
    USER_NOT_FOUND,
    EMAIL_ALREADY_EXIST,
    NETWORK_ERROR,
    TOO_MANY_REQUESTS,
    UNKNOWN
}