package com.example.studyassistant.studytracker.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteDialog(
    title: String,
    bodyText: String,
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmationButtonClick: () -> Unit,
) {
    if(isOpen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title)},
            text = { Text(text = bodyText) },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmationButtonClick
                ) {
                    Text(text = "Delete")
                }
            }
        )
    }
}