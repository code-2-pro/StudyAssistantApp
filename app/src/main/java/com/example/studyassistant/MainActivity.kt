package com.example.studyassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studyassistant.Route.DashboardScreen
import com.example.studyassistant.Route.SessionScreen
import com.example.studyassistant.Route.SubjectScreen
import com.example.studyassistant.Route.TaskScreen
import com.example.studyassistant.studytracker.domain.model.Session
import com.example.studyassistant.studytracker.domain.model.Subject
import com.example.studyassistant.studytracker.domain.model.Task
import com.example.studyassistant.studytracker.presentation.dashboard.DashboardScreen
import com.example.studyassistant.studytracker.presentation.dashboard.DashboardViewModel
import com.example.studyassistant.studytracker.presentation.session.SessionScreen
import com.example.studyassistant.studytracker.presentation.session.SessionViewModel
import com.example.studyassistant.studytracker.presentation.subject.SubjectScreen
import com.example.studyassistant.studytracker.presentation.subject.SubjectViewModel
import com.example.studyassistant.studytracker.presentation.task.TaskScreen
import com.example.studyassistant.studytracker.presentation.task.TaskViewModel
import com.example.studyassistant.ui.theme.StudyAssistantTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.emptyFlow


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyAssistantTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = DashboardScreen,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<DashboardScreen> {
                            val viewModel: DashboardViewModel = hiltViewModel()
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            val tasks by viewModel.tasks.collectAsStateWithLifecycle()
                            val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()
                            DashboardScreen(
                                state = state,
                                tasks = tasks,
                                recentSessions = recentSessions,
                                onAction = viewModel::onAction,
                                snackbarEvent = emptyFlow(),
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
                            SubjectScreen(
                                state = state,
                                snackbarEvent = emptyFlow(),
                                onAction = viewModel::onAction,
                                onBackButtonClick = { navController.navigateUp() },
                                onAddTaskButtonClick = {
                                    navController.navigate(TaskScreen(
                                        taskId = null,
                                        subjectId = -1
                                    ))
                                },
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
                            TaskScreen(
                                onBackButtonClick = { navController.navigateUp() }
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