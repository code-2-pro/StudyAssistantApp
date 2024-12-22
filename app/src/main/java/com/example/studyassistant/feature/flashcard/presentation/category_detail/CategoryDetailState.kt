package com.example.studyassistant.feature.flashcard.presentation.category_detail

import androidx.compose.ui.graphics.Color
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard
import com.example.studyassistant.feature.flashcard.domain.model.FlashcardCategory

data class CategoryDetailState(
    val isLoading: Boolean = false,
    val currentCategoryId: String = "",
    val categoryName: String = "",
    val hasMeaning: Boolean = false,
    val categoryCardColors: List<Color> = FlashcardCategory.categoryCardColors.last(),
    val flashcards: List<Flashcard> = emptyList()
)
