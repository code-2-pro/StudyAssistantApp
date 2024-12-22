package com.example.studyassistant.navigation.graph.flashcard.route

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.feature.flashcard.presentation.category_detail.CategoryDetailScreen
import com.example.studyassistant.feature.flashcard.presentation.category_detail.CategoryDetailViewModel
import com.example.studyassistant.feature.flashcard.presentation.category_detail.components.CategoryDetailScreenTopBar

@Composable
fun NavGraphBuilder.CategoryDetailRoute(
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
){
    val viewModel: CategoryDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    var isGenerateCardDialogOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = {
                CategoryDetailScreenTopBar(
                    state = state,
                    isOnline = isOnline,
                    onBackButtonClicked = {
                        navController.navigateUp()
                    },
                    onGenerateCardClicks = {
                        isGenerateCardDialogOpen = true
                    },
                )
            },
            fabContent = {
                if(!state.isLoading){
                    ExtendedFloatingActionButton(
                        onClick = {
                            navController.navigate(
                                Route.FlashcardScreen(
                                    flashcardId = null,
                                    categoryId = state.currentCategoryId
                                )
                            )
                        },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Add Flashcard") },
                        text = { Text("Add Flashcard") }
                    )
                }
            },
            scaffoldModifier = Modifier
        ))
    }

    CategoryDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        isGenerateCardDialogOpen = isGenerateCardDialogOpen,
        onGenerateCardDialogVisibleChange = { isGenerateCardDialogOpen = it },
    )
}