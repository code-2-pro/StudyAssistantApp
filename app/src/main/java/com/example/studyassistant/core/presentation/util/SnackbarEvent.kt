package com.example.studyassistant.core.presentation.util

import androidx.compose.material3.SnackbarDuration

sealed class SnackbarEvent{
    data class ShowSnackBar(
        val message: String,
        val duration: SnackbarDuration = SnackbarDuration.Short
    ) : SnackbarEvent()
}