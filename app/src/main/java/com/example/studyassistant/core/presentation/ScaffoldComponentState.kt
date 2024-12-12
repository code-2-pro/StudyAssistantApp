package com.example.studyassistant.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ScaffoldComponentState(
    var topBarContent: @Composable () -> Unit = {},
    var fabContent: @Composable () -> Unit = {},
    var bottomBarContent: @Composable () -> Unit = {},
    var scaffoldModifier: Modifier = Modifier
)
