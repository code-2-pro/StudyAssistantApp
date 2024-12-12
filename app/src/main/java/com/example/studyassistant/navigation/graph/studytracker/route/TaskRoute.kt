package com.example.studyassistant.navigation.graph.studytracker.route

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
import com.example.studyassistant.feature.studytracker.presentation.task.TaskAction
import com.example.studyassistant.feature.studytracker.presentation.task.TaskScreen
import com.example.studyassistant.feature.studytracker.presentation.task.TaskScreenTopBar
import com.example.studyassistant.feature.studytracker.presentation.task.TaskViewModel

@Composable
fun NavGraphBuilder.TaskRoute(
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {

    val viewModel: TaskViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = {
                TaskScreenTopBar(
                    isTaskExist = state.currentTaskId.isNotBlank(),
                    isComplete = state.isTaskComplete,
                    checkBoxBorderColor = state.priority.color,
                    onBackButtonClick = { navController.navigateUp() },
                    onDeleteButtonClick = { isDeleteDialogOpen = true },
                    onCheckBoxClick = { viewModel.onAction(TaskAction.OnIsCompleteChange)}
                )
            },
            fabContent = { },
            bottomBarContent = {},
            scaffoldModifier = Modifier
        ))
    }

    TaskScreen(
        state = state,
        isDeleteDialogOpen = isDeleteDialogOpen,
        onDeleteDialogVisibleChange = { isDeleteDialogOpen = it },
        onAction = viewModel::onAction
    )

}