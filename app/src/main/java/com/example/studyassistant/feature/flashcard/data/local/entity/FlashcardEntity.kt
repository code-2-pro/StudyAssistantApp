package com.example.studyassistant.feature.flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FlashcardEntity(

    val flashcardCategoryId: String,
    val question: String,
    val answer: String,

    val createdAt: Long,
    @PrimaryKey()
    val flashcardId: String,
)
