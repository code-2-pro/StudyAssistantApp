package com.example.studyassistant.feature.flashcard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FlashcardEntity(
    val question: String,
    val answer: String,
    @PrimaryKey()
    val flashcardId: String,
)
