package com.example.studyassistant.core.domain.util

enum class AuthError: Error {
    EMAIL_IS_BLANK,
    PASSWORD_IS_BLANK,
    WEAK_PASSWORD,
    INVALID_EMAIL,
    INVALID_LOGIN_CREDENTIALS,
    USER_NOT_FOUND,
    EMAIL_ALREADY_EXIST,
    REQUIRES_RECENT_LOGIN,
    NETWORK_ERROR,
    TOO_MANY_REQUESTS,
    INVALID_PASSWORD,
    USER_DISABLED,
    ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL,
    CUSTOM_TOKEN_MISMATCH,
    INVALID_CUSTOM_TOKEN,
    CREDENTIAL_ALREADY_IN_USE,
    OPERATION_NOT_ALLOWED,
    MISSING_MFA_INFO,
    MISSING_MFA_PENDING_CREDENTIAL,
    INVALID_MFA_SESSION,
    INVALID_MFA_PENDING_CREDENTIAL,
    SECOND_FACTOR_EXISTS,
    SECOND_FACTOR_LIMIT_EXCEEDED,
    UNKNOWN
}