@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.presentation.dashboard.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DashboardScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Study Assistant",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}