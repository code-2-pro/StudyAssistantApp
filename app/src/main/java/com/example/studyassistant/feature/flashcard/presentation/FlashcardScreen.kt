package com.example.studyassistant.feature.flashcard.presentation

import androidx.compose.runtime.Composable
import com.example.studyassistant.feature.flashcard.presentation.components.FlashcardPager

@Composable
fun FlashcardScreen(
    state: FlashcardState
) {
    FlashcardPager(
        flashcards = state.flashcards,
    )
}
