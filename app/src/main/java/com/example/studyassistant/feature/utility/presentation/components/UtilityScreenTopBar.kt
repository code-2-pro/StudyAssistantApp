@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.feature.utility.presentation.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun UtilityScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Utility",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}