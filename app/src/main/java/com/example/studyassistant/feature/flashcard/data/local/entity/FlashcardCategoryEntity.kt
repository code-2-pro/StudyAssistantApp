package com.example.studyassistant.feature.flashcard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FlashcardCategoryEntity(
    val name: String,
    val isMeaningful: Boolean,
    val colors: List<Int>,
    @PrimaryKey()
    val categoryId: String,
)