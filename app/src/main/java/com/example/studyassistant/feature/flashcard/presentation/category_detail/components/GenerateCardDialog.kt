package com.example.studyassistant.feature.flashcard.presentation.category_detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.core.domain.util.validateCardQuantity
import com.example.studyassistant.core.presentation.util.toString

@Composable
fun GenerateCardDialog(
    title: String = "Generate Flashcard",
    isOpen: Boolean,
    categoryName: String,
    onDismissRequest: () -> Unit,
    onGenerateButtonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var cardQuantityError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var cardQuantity by remember{
        mutableStateOf("")
    }

    val context = LocalContext.current
    val cardQuantityResult = validateCardQuantity(cardQuantity)

    cardQuantityError = when (cardQuantityResult) {
        is Result.Error -> cardQuantityResult.error.toString(context)
        is Result.Success -> null
    }

    if(isOpen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {Text(text = title)},
            text = {
                Column()
                {
                    Text(text = categoryName)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = cardQuantity,
                        onValueChange = { cardQuantity = it },
                        label = {Text(text = "Card Quantity")},
                        singleLine = true,
                        isError = cardQuantityError != null && cardQuantity.isNotBlank(),
                        supportingText =  { Text(text = cardQuantityError.orEmpty()) }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onGenerateButtonClick(cardQuantity.toInt()) },
                    enabled = cardQuantityError == null
                ) {
                    Text(text = "Generate")
                }
            }
        )
    }
}