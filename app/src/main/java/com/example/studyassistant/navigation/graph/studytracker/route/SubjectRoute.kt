@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.navigation.graph.studytracker.route

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.feature.studytracker.presentation.subject.SubjectScreen
import com.example.studyassistant.feature.studytracker.presentation.subject.SubjectScreenTopBar
import com.example.studyassistant.feature.studytracker.presentation.subject.SubjectViewModel

@Composable
fun NavGraphBuilder.SubjectRoute(
    updateScaffold: (ScaffoldComponentState) -> Unit,
    subjectIdOnTimerService: String?,
    navController: NavController,
    modifier: Modifier = Modifier
){

    val viewModel: SubjectViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var isEditSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()
    // Manage the FAB state in the parent
    var isFABExpanded by remember { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(key1 = true) {
        updateScaffold(
            ScaffoldComponentState(
                topBarContent = {
                    SubjectScreenTopBar(
                        title = state.subjectName,
                        isSubjectTimerServiceOn = subjectIdOnTimerService == state.currentSubjectId,
                        onBackButtonClick = { navController.navigateUp() },
                        onDeleteButtonClick = { isDeleteSubjectDialogOpen = true },
                        onEditButtonClick = { isEditSubjectDialogOpen = true },
                        scrollBehavior = scrollBehavior,
                    )
                },
                fabContent = {
                    ExtendedFloatingActionButton(
                        onClick = {
                            navController.navigate(
                                Route.TaskScreen(
                                    taskId = null,
                                    subjectId = state.currentSubjectId
                                )
                            )
                        },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Add Task") },
                        text = { Text("Add Task") },
                        expanded = isFABExpanded
                    )
                },
                scaffoldModifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        )
    }

    SubjectScreen(
        state = state,
        listState = listState,
        onListScrolled = { firstVisibleItemIndex ->
            isFABExpanded = firstVisibleItemIndex == 0
        },
        isEditSubjectDialogOpen = isEditSubjectDialogOpen,
        isDeleteSubjectDialogOpen = isDeleteSubjectDialogOpen,
        onEditSubjectDialogVisibleChange = { isEditSubjectDialogOpen = it },
        onDeleteSubjectDialogVisibleChange = { isDeleteSubjectDialogOpen = it },
        onAction = viewModel::onAction,
        onDeleteButtonClick = { navController.navigateUp() },
        onTaskCardClick = { taskId ->
            navController.navigate(Route.TaskScreen(
                taskId = taskId,
                subjectId = null
            ))
        }
    )
}