@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.feature.flashcard.presentation.category.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.example.studyassistant.feature.flashcard.presentation.category.CategoryState

@Composable
fun CategoryScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Flashcard Category",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}

