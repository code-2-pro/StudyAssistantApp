package com.example.studyassistant.feature.setting.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RemoteDataDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onKeepLocalDataClick: () -> Unit,
    onRemoveLocalDataClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                    TextButton(
                        onClick = onRemoveLocalDataClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error // Standard red
                        )
                    ) {
                        Text(
                            text = "Remove",
                            color = MaterialTheme.colorScheme.onError // Contrast text color
                        )
                    }
                    TextButton(
                        onClick = onKeepLocalDataClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary // Standard blue
                        )
                    ) {
                        Text(
                            text = "Keep",
                            color = MaterialTheme.colorScheme.onPrimary // Contrast text color
                        )
                    }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(text = "Keep Local Data?")
            },
            text = {
                Text(
                    text = "Do you want to remove local data when logging out, " +
                            "or keep it for offline use without an account?"
                )
            },
            modifier = modifier
        )
    }
}