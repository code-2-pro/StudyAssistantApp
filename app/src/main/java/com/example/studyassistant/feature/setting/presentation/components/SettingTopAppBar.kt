package com.example.studyassistant.feature.setting.presentation.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenTopBar(
    appBarElevation: Dp,
    hasScrolled: Boolean,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Settings", style = MaterialTheme.typography.headlineSmall) },
        modifier = Modifier.shadow(appBarElevation),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (hasScrolled) 1f else 0f)
            } else {
                MaterialTheme.colorScheme.surface
            },
        )
    )
}