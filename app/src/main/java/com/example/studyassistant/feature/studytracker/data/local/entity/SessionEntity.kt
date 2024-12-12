package com.example.studyassistant.feature.studytracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SessionEntity(
    val sessionSubjectId: String,
    val relatedToSubject: String,
    val date: Long,
    val duration: Long,
    @PrimaryKey()
    val sessionId: String
)
