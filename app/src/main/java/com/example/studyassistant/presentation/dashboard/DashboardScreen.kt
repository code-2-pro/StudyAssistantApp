package com.example.studyassistant.presentation.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.studyassistant.presentation.dashboard.components.DashboardScreenTopBar

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {
    Scaffold (
        topBar = { DashboardScreenTopBar() }

    ) { paddingValue ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {

        }
    }
}

