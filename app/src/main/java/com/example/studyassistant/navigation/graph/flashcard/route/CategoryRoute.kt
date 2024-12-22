package com.example.studyassistant.navigation.graph.flashcard.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.feature.flashcard.presentation.category.CategoryScreen
import com.example.studyassistant.feature.flashcard.presentation.category.CategoryViewModel
import com.example.studyassistant.feature.flashcard.presentation.category.components.CategoryScreenTopBar

@Composable
fun NavGraphBuilder.CategoryRoute(
    updateScaffold: (ScaffoldComponentState) -> Unit,
    modifier: Modifier = Modifier
){
    val viewModel: CategoryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = { CategoryScreenTopBar() },
            fabContent = { },
            scaffoldModifier = Modifier
        ))
    }

    CategoryScreen(
        state = state,
        onAction = viewModel::onAction
    )
}