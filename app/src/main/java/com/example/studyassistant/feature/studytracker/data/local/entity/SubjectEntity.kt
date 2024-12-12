package com.example.studyassistant.feature.studytracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SubjectEntity(
    val name: String,
    val goalHours: Float,
    val colors: List<Int>,
    @PrimaryKey()
    val subjectId: String
)

