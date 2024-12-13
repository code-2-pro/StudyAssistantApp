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
import com.example.studyassistant.feature.flashcard.presentation.FlashcardScreen
import com.example.studyassistant.feature.flashcard.presentation.FlashcardViewModel
import com.example.studyassistant.feature.flashcard.presentation.components.FlashScreenTopBar

@Composable
fun NavGraphBuilder.FlashcardRoute(
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
){
    val viewModel: FlashcardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = { FlashScreenTopBar() },
            fabContent = { },
            scaffoldModifier = Modifier
        ))
    }

//    var addOnce = remember {
//        mutableStateOf(false)
//    }
//
//    if(addOnce.value == false) {
//        (1..10).forEach {
//            viewModel.addFlashcard(
//                Flashcard(
//                    question = "question $it",
//                    answer = "answer $it"
//                )
//            )
//        }
//        addOnce.value = true
//    }

    FlashcardScreen(
        state = state
    )
}