package com.example.studyassistant.feature.authentication.data.mapper

import com.example.studyassistant.feature.authentication.domain.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUser(): User {
    return User(
        email = email?: "",
        displayName = displayName,
        userId = uid
    )
}