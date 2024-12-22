package com.example.studyassistant.feature.flashcard.presentation.category

import androidx.compose.ui.graphics.Color

sealed interface CategoryAction {
    object SaveCategory: CategoryAction
    data class UpdateCategory(val categoryId: String, val categoryPreviousName: String): CategoryAction
    data class DeleteCategory(val categoryId: String): CategoryAction
    data class GoToCategoryDetail(val categoryId: String): CategoryAction
    data class ShowFlashcards(val categoryId: String): CategoryAction
    data class OnCancelCategoryChanges(
        val previousName: String,
        val previousColor: List<Color>
    ): CategoryAction
    data class OnCategoryNameChange(val name: String): CategoryAction
    data class OnCategoryCardColorChange(val color: List<Color>): CategoryAction
}