package com.example.studyassistant.feature.authentication.presentation.login.componets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.studyassistant.ui.theme.StudyAssistantTheme

@Composable
fun SyncOptionDialog(
    text: String,
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSendLocalDataClick: () -> Unit,
    onGetRemoteDataClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if(isOpen){
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onSendLocalDataClick) {
                    Text(
                        text = "Keep Local",
                        fontSize = 12.sp
                    )
                }
                TextButton(
                    onClick = onGetRemoteDataClick,
                ) {
                    Text(
                        text = "Keep Remote",
                        fontSize = 12.sp
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        fontSize = 12.sp
                        )
                }
            },
            title = {
                Text(text = "Data Conflict")
            },
            text = {
                Text( "Local and remote data do not match. " +
                        "Choose to keep either the local or remote data." +
                        " This will replace the other.\n" + text
                )
            },
            modifier = modifier
        )
    }
}

@Preview
@Composable
private fun SyncOptionDialogPreview() {
    StudyAssistantTheme {
        SyncOptionDialog(
            text = "",
            isOpen = true,
            onDismiss = {},
            onSendLocalDataClick = {},
            onGetRemoteDataClick = {},
        )
    }
}