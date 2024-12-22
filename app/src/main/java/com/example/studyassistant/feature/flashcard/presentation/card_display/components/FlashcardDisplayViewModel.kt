package com.example.studyassistant.feature.flashcard.presentation.card_display.components

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardCategoryRepository
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardRepository
import com.example.studyassistant.feature.flashcard.presentation.card_display.FlashcardDisplayState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardDisplayViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val categoryRepository: FlashcardCategoryRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val navArgs = savedStateHandle.toRoute<Route.FlashcardDisplayScreen>()

    val _state = MutableStateFlow(FlashcardDisplayState())
    val state = _state.asStateFlow()

    init {
        fetchFlashcards()
        fetchCategory()
    }

    private fun fetchFlashcards() {
        viewModelScope.launch{
            val flashcards = flashcardRepository.getFlashcardsForCategory(navArgs.categoryId)
            _state.value = _state.value.copy(
                flashcards = flashcards.first()
            )
        }
    }

    private fun fetchCategory() {
        viewModelScope.launch{
            val category = categoryRepository.getCategoryById(navArgs.categoryId)
            category.first()?.let {
                _state.value = _state.value.copy(
                    categoryName = it.name
                )
            }
        }
    }

}