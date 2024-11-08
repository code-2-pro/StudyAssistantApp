package com.example.studyassistant.presentation.dashboard.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DeleteDialog(
    title: String,
    bodyText: String,
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmationButtonClick: () -> Unit,
    modifier: Modifier = Modifier
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