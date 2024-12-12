package com.example.studyassistant.feature.flashcard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository
): ViewModel() {

    val _state = MutableStateFlow(FlashcardState())
    val state = combine(
        _state,
        flashcardRepository.getAllFlashcards(),
    ){ state, flashcards ->
        state.copy(
            flashcards = flashcards
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FlashcardState()
    )

    fun onAction(action: FlashcardAction){
        when(action){
            FlashcardAction.SaveFlashcard -> { }
            is FlashcardAction.DeleteFlashcard -> removeFlashcard(action.flashcard)
        }
    }

    fun addFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            flashcardRepository.addFlashcard(
                Flashcard(
                    question = flashcard.question,
                    answer = flashcard.answer,
                    flashcardId = UUID.randomUUID().toString()
                )
            )
            _state.update {
                it.copy(
                    question = "",
                    answer = ""
                )
            }
        }
    }

    fun removeFlashcard(flashcard: Flashcard) {
        viewModelScope.launch { flashcardRepository.removeFlashcard(flashcard) }
    }

}