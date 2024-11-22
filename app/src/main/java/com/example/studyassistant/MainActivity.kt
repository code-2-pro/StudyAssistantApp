@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyassistant.core.navigation.NavigationAction
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route.DashboardScreen
import com.example.studyassistant.core.navigation.Route.SessionScreen
import com.example.studyassistant.core.navigation.Route.SubjectScreen
import com.example.studyassistant.core.navigation.Route.TaskScreen
import com.example.studyassistant.core.presentation.util.ObserveAsEvents
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.studytracker.domain.model.Session
import com.example.studyassistant.studytracker.domain.model.Subject
import com.example.studyassistant.studytracker.domain.model.Task
import com.example.studyassistant.studytracker.presentation.dashboard.DashboardScreen
import com.example.studyassistant.studytracker.presentation.dashboard.DashboardScreenTopBar
import com.example.studyassistant.studytracker.presentation.dashboard.DashboardViewModel
import com.example.studyassistant.studytracker.presentation.session.SessionScreen
import com.example.studyassistant.studytracker.presentation.session.SessionViewModel
import com.example.studyassistant.studytracker.presentation.subject.SubjectScreen
import com.example.studyassistant.studytracker.presentation.subject.SubjectScreenTopBar
import com.example.studyassistant.studytracker.presentation.subject.SubjectViewModel
import com.example.studyassistant.studytracker.presentation.task.TaskAction
import com.example.studyassistant.studytracker.presentation.task.TaskScreen
import com.example.studyassistant.studytracker.presentation.task.TaskScreenTopBar
import com.example.studyassistant.studytracker.presentation.task.TaskViewModel
import com.example.studyassistant.ui.theme.StudyAssistantTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyAssistantTheme {
                var topBarContent by remember {
                    mutableStateOf <@Composable () -> Unit> ({ })
                }
                // Workaround to set null for FAB
                var fabContent by remember {
                    mutableStateOf <@Composable (() -> Unit)> ({ })
                }
                var scaffoldModifier by remember { mutableStateOf<Modifier> (Modifier) }

                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                ObserveAsEvents(
                    events = SnackbarController.events,
                    snackbarHostState
                ) { event ->
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()

                        val result = snackbarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = event.action?.name,
                        )

                        if(result == SnackbarResult.ActionPerformed) {
                            event.action?.action?.invoke()
                        }
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    modifier = scaffoldModifier.fillMaxSize(),
                    topBar =  topBarContent,
                    floatingActionButton = fabContent,
                ) { innerPadding ->
                    val navController = rememberNavController()

                    ObserveAsEvents(events = navigator.navigationActions) { action ->
                        when(action) {
                            is NavigationAction.Navigate -> navController.navigate(
                                action.route
                            ) {
                                action.navOptions(this)
                            }
                            NavigationAction.NavigateUp -> navController.navigateUp()
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = navigator.startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<DashboardScreen> {
                            val viewModel: DashboardViewModel = hiltViewModel()
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            val tasks by viewModel.tasks.collectAsStateWithLifecycle()
                            val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()

                            topBarContent = { DashboardScreenTopBar() }
                            fabContent = { }
                            scaffoldModifier = Modifier

                            DashboardScreen(
                                state = state,
                                tasks = tasks,
                                recentSessions = recentSessions,
                                onAction = viewModel::onAction,
                                onSubjectCardClick = { subjectId ->
                                    subjectId?.let {
                                        navController.navigate(SubjectScreen(subjectId = subjectId))
                                    }
                                },
                                onTaskCardClick = { taskId ->
                                    navController.navigate(TaskScreen(
                                        taskId = taskId,
                                        subjectId = null
                                    ))
                                },
                                onStartSessionButtonClick = {
                                    navController.navigate(SessionScreen)
                                },
                            )
                        }
                        composable<SubjectScreen> {
                            val viewModel: SubjectViewModel = hiltViewModel()
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                            var isEditSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
                            var isDeleteSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
                            val listState = rememberLazyListState()
                            // Manage the FAB state in the parent
                            var isFABExpanded by remember { mutableStateOf(true) }

                            topBarContent = {
                                SubjectScreenTopBar(
                                    title = state.subjectName,
                                    onBackButtonClick = { navController.navigateUp() },
                                    onDeleteButtonClick = { isDeleteSubjectDialogOpen = true },
                                    onEditButtonClick = { isEditSubjectDialogOpen = true },
                                    scrollBehavior = scrollBehavior,
                                )
                            }
                            fabContent = {
                                ExtendedFloatingActionButton(
                                    onClick = {
                                        navController.navigate(
                                            TaskScreen(
                                                taskId = null,
                                                subjectId = state.currentSubjectId)
                                        )
                                    },
                                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Task") },
                                    text = { Text("Add Task") },
                                    expanded = isFABExpanded
                                )
                            }
                            scaffoldModifier = Modifier
                                .nestedScroll(scrollBehavior.nestedScrollConnection)

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
                                    navController.navigate(TaskScreen(
                                        taskId = taskId,
                                        subjectId = null
                                    ))
                                }
                            )
                        }
                        composable<TaskScreen> {
                            val viewModel: TaskViewModel = hiltViewModel()
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }
                            topBarContent = {
                                TaskScreenTopBar(
                                    isTaskExist = state.currentTaskId != null,
                                    isComplete = state.isTaskComplete,
                                    checkBoxBorderColor = state.priority.color,
                                    onBackButtonClick = { navController.navigateUp() },
                                    onDeleteButtonClick = { isDeleteDialogOpen = true },
                                    onCheckBoxClick = { viewModel.onAction(TaskAction.OnIsCompleteChange) }
                                )
                            }
                            fabContent = { }
                            scaffoldModifier = Modifier
                            TaskScreen(
                                state = state,
                                isDeleteDialogOpen = isDeleteDialogOpen,
                                onDeleteDialogVisibleChange = { isDeleteDialogOpen = it },
                                onAction = viewModel::onAction,
                                onDeleteButtonClick  = { navController.navigateUp() }
                            )
                        }
                        composable<SessionScreen> {
                            val viewModel: SessionViewModel = hiltViewModel()
                            SessionScreen(
                                onBackButtonClicked = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}

val subjects = listOf(
    Subject(
        name = "English",
        goalHours = 10f,
        colors = Subject.subjectCardColors[0].map{ it.toArgb() },
        subjectId = 0
    ),
    Subject(
        name = "Physics",
        goalHours = 10f,
        colors = Subject.subjectCardColors[1].map{ it.toArgb() },
        subjectId = 0
    ),
    Subject(
        name = "Maths",
        goalHours = 10f,
        colors = Subject.subjectCardColors[2].map{ it.toArgb() },
        subjectId = 0
    ),
    Subject(
        name = "Geology",
        goalHours = 10f,
        colors = Subject.subjectCardColors[3].map{ it.toArgb() },
        subjectId = 0
    ),
    Subject(
        name = "Fine Arts",
        goalHours = 10f,
        colors = Subject.subjectCardColors[4].map{ it.toArgb() },
        subjectId = 0
    ),
)

val tasks = listOf(
    Task(
        title = "Prepare notes",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1
    ),
    Task(
        title = "Do Homework",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = true,
        taskSubjectId = 0,
        taskId = 1
    ),
    Task(
        title = "Go Coaching",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1
    ),
    Task(
        title = "Assignment",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1
    ),
    Task(
        title = "Write Poem",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isComplete = true,
        taskSubjectId = 0,
        taskId = 1
    )
)

val sessions = listOf(
    Session(
        relatedToSubject = "English",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "English",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "Physics",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "Maths",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "English",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0
    )
)