package com.example.studyassistant.feature.authentication.domain.model

data class User(
    val email: String = "",
    val displayName: String? = "",
    val userId: String? = null
)
