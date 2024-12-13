package com.example.studyassistant.navigation.graph.utillity.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.feature.utility.presentation.assistant.AssistantScreen
import com.example.studyassistant.feature.utility.presentation.assistant.AssistantViewModel
import com.example.studyassistant.feature.utility.presentation.assistant.components.AssistantScreenTopBar

@Composable
fun NavGraphBuilder.AssistantRoute(
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: AssistantViewModel = hiltViewModel()

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = {
                AssistantScreenTopBar(onBackButtonClicked = { navController.navigateUp() })
            },
            fabContent = { },
            scaffoldModifier = Modifier
        ))
    }

    AssistantScreen(
        messageList = viewModel.messageList,
        onMessageSend = viewModel::sendMessage
    )
}