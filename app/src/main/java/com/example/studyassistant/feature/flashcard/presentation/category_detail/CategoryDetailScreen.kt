package com.example.studyassistant.feature.flashcard.presentation.category_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.studyassistant.feature.flashcard.presentation.card.components.flashcardList
import com.example.studyassistant.feature.flashcard.presentation.category_detail.components.GenerateCardDialog

@Composable
fun CategoryDetailScreen(
    isGenerateCardDialogOpen: Boolean,
    onGenerateCardDialogVisibleChange: (Boolean) -> Unit,
    state: CategoryDetailState,
    onAction: (CategoryDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    GenerateCardDialog(
        isOpen = isGenerateCardDialogOpen,
        categoryName = state.categoryName,
        onDismissRequest = {
            onGenerateCardDialogVisibleChange(false)
        },
        onGenerateButtonClick = { cardQuantity ->
            onAction(CategoryDetailAction.GenerateFlashcard(cardQuantity))
            onGenerateCardDialogVisibleChange(false)
        },
    )

    if(state.isLoading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }else{
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            flashcardList(
                sectionTitle = "FLASHCARD LIST",
                emptyListText = "You don't have any flashcards yet.\n " +
                        "Click the + button to add new card.",
                cards = state.flashcards,
                onEditButtonClick = { flashcardId ->
                    onAction(CategoryDetailAction.GoToFlashcardScreen(flashcardId))
                }
            )
        }
    }


}