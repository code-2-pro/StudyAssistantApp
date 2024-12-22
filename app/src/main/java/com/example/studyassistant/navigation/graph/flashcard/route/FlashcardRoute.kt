package com.example.studyassistant.navigation.graph.flashcard.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.feature.flashcard.presentation.card.FlashcardScreen
import com.example.studyassistant.feature.flashcard.presentation.card.FlashcardViewModel
import com.example.studyassistant.feature.flashcard.presentation.card.components.FlashcardScreenTopBar

@Composable
fun NavGraphBuilder.FlashcardRoute(
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
){
    val viewModel: FlashcardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = { FlashcardScreenTopBar(
                isCardExist = state.currentFlashcardId.isNotBlank(),
                onBackButtonClick = { navController.navigateUp() },
                onDeleteButtonClick = { isDeleteDialogOpen = true }
            ) },
            fabContent = { },
            scaffoldModifier = Modifier
        ))
    }

    FlashcardScreen(
        state = state,
        onAction = viewModel::onAction,
        isDeleteDialogOpen = isDeleteDialogOpen,
        onDeleteDialogVisibleChange = { isDeleteDialogOpen = it },
    )
}