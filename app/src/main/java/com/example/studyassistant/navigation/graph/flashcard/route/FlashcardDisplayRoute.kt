package com.example.studyassistant.navigation.graph.flashcard.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.feature.flashcard.presentation.card_display.FlashcardDisplayScreen
import com.example.studyassistant.feature.flashcard.presentation.card_display.components.FlashcardDisplayTopBar
import com.example.studyassistant.feature.flashcard.presentation.card_display.components.FlashcardDisplayViewModel

@Composable
fun NavGraphBuilder.FlashcardDisplayRoute(
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
){
    val viewModel: FlashcardDisplayViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = {
                FlashcardDisplayTopBar(
                    onBackButtonClicked = {
                        navController.navigateUp()
                    }
                )
            },
            fabContent = { },
            scaffoldModifier = Modifier
        ))
    }

    FlashcardDisplayScreen(
        state = state
    )
}