package com.example.studyassistant.navigation.graph.studytracker.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.navigation.Route.SubjectScreen
import com.example.studyassistant.core.navigation.Route.TaskScreen
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.core.presentation.components.BottomBarNavigation
import com.example.studyassistant.feature.studytracker.presentation.dashboard.DashboardScreen
import com.example.studyassistant.feature.studytracker.presentation.dashboard.DashboardScreenTopBar
import com.example.studyassistant.feature.studytracker.presentation.dashboard.DashboardViewModel

@Composable
fun NavGraphBuilder.DashboardRoute(
    selectedItemIndex: Int,
    onSelectedItemIndexChange :(Int) -> Unit,
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
){
    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = { DashboardScreenTopBar() },
            fabContent = { },
            bottomBarContent = {
                BottomBarNavigation(
                    selectedItemIndex = selectedItemIndex,
                    onSelectedItemIndexChange = { onSelectedItemIndexChange(it) },
                    navController = navController
                )
            },
            scaffoldModifier = Modifier
        ))
    }

        DashboardScreen(
            state = state,
            tasks = tasks,
            recentSessions = recentSessions,
            onAction = viewModel::onAction,
            onSubjectCardClick = { subjectId ->
                subjectId?.let {
                    navController.navigate(
                        SubjectScreen(subjectId = subjectId)
                    )
                }
            },
            onTaskCardClick = { taskId ->
                navController.navigate(
                    TaskScreen(
                        taskId = taskId,
                        subjectId = null
                    )
                )
            },
            onStartSessionButtonClick = {
                navController.navigate(Route.SessionScreen)
            },
        )

}