package com.example.studyassistant.navigation.graph.utillity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.feature.utility.presentation.UtilityScreen
import com.example.studyassistant.feature.utility.presentation.components.UtilityScreenTopBar

@Composable
fun NavGraphBuilder.UtilityRoute(
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = {
                UtilityScreenTopBar()
            },
            fabContent = { },
            scaffoldModifier = Modifier
        ))
    }

    UtilityScreen(
        onAssistantClick = { navController.navigate(Route.AssistantScreen) }
    )

}