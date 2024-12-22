package com.example.studyassistant.feature.flashcard.presentation.category.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.core.domain.util.validateFlashcardCategoryName
import com.example.studyassistant.core.presentation.util.toString
import com.example.studyassistant.feature.flashcard.domain.model.FlashcardCategory

@Composable
fun SaveCategoryDialog(
    title: String = "Add/Update Category",
    isOpen: Boolean,
    selectedColors: List<Color>,
    categoryName: String,
    onColorChange: (List<Color>) -> Unit,
    onCategoryNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmationButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var categoryNameError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current
    val categoryNameResult = validateFlashcardCategoryName(categoryName)

    categoryNameError = when (categoryNameResult) {
        is Result.Error -> categoryNameResult.error.toString(context)
        is Result.Success -> null
    }

    if(isOpen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {Text(text = title)},
            text = {
                Column() {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        FlashcardCategory.categoryCardColors.forEach{ colors ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = if (colors == selectedColors) Color.Black
                                        else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .background(brush = Brush.verticalGradient(colors))
                                    .clickable { onColorChange(colors) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = onCategoryNameChange,
                        label = {Text(text = "Category Name")},
                        singleLine = true,
                        isError = categoryNameError != null && categoryName.isNotBlank(),
                        supportingText =  { Text(text = categoryNameError.orEmpty()) }
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
                    onClick = onConfirmationButtonClick,
                    enabled = categoryNameError == null
                ) {
                    Text(text = "Save")
                }
            }
        )
    }
}