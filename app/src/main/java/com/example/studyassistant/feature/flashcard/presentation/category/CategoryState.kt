package com.example.studyassistant.feature.flashcard.presentation.category

import androidx.compose.ui.graphics.Color
import com.example.studyassistant.feature.flashcard.domain.model.CategoryWithFlashcardCount
import com.example.studyassistant.feature.flashcard.domain.model.FlashcardCategory

data class CategoryState(
    val isLoading: Boolean = false,
    val currentCategoryId: String = "",
    val categoryName: String = "",
    val categoryPreviousName: String = "",
    val categoryCardColors: List<Color> = FlashcardCategory.categoryCardColors.last(),
    val categoriesWithFlashcardCount: List<CategoryWithFlashcardCount> = emptyList()
)
