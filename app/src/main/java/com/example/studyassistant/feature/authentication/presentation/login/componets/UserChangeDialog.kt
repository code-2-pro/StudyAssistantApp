package com.example.studyassistant.feature.authentication.presentation.login.componets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun UserChangeDialog(
    isOpen : Boolean,
    userIsReplacedEmail: String,
    onDismiss: () -> Unit,
    onUseNoAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if(isOpen){
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onUseNoAccountClick) {
                    Text(
                        text = "Proceed",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(text = "Local Data Found")
            },
            text = {
                Text(
                    text = "Data for user $userIsReplacedEmail exists on this device. " +
                            "Choosing this will delete the local data. Proceed?"
                )
            },
            modifier = modifier
        )
    }
}