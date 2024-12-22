package com.example.studyassistant.feature.flashcard.presentation.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.core.domain.util.validateFlashcardInput
import com.example.studyassistant.core.presentation.util.toString
import com.example.studyassistant.feature.studytracker.presentation.components.DeleteDialog

@Composable
fun FlashcardScreen(
    state: FlashcardState,
    isDeleteDialogOpen: Boolean,
    onDeleteDialogVisibleChange: (Boolean) -> Unit,
    onAction: (FlashcardAction) -> Unit
) {

    var cardQuestionError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var cardAnswerError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current
    val cardQuestionResult = validateFlashcardInput(state.question)
    val cardAnswerResult = validateFlashcardInput(state.answer)

    cardQuestionError = when (cardQuestionResult) {
        is Result.Error -> cardQuestionResult.error.toString(context)
        is Result.Success -> null
    }

    cardAnswerError = when (cardAnswerResult) {
        is Result.Error -> cardAnswerResult.error.toString(context)
        is Result.Success -> null
    }

    DeleteDialog(
        title = "Delete Flashcard?",
        bodyText = "Are you sure, you want to delete this card? " +
                "This action can not be undone.",
        isOpen = isDeleteDialogOpen,
        onDismissRequest = { onDeleteDialogVisibleChange(false) },
        onConfirmationButtonClick = {
            onAction(FlashcardAction.DeleteFlashcard)
            onDeleteDialogVisibleChange(false)
        },
    )

    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.question,
            onValueChange = { onAction(FlashcardAction.OnQuestionChange(it)) },
            label = {Text(text = "Question")},
            isError = cardQuestionError != null && state.question.isNotBlank(),
            supportingText = {Text(text = cardQuestionError.orEmpty() )}
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.answer,
            onValueChange = { onAction(FlashcardAction.OnAnswerChange(it)) },
            label = {Text(text = "Answer")},
            isError = cardAnswerError != null && state.answer.isNotBlank(),
            supportingText = {Text(text = cardAnswerError.orEmpty() )}
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Related to category",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment =  Alignment.CenterVertically
        ) {
            Text(
                text = state.relatedToCategory,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Button(
            enabled = cardQuestionError == null && cardAnswerError == null,
            onClick = { onAction(FlashcardAction.SaveFlashcard) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Text(text = "Save")
        }
    }

}

