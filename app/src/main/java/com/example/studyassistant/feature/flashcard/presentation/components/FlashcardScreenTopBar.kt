@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.feature.flashcard.presentation.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun FlashScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Flashcard",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}