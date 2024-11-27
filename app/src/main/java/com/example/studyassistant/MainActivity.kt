@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.studyassistant.core.navigation.NavigationAction
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route.DashboardScreen
import com.example.studyassistant.core.navigation.Route.SessionScreen
import com.example.studyassistant.core.navigation.Route.SubjectScreen
import com.example.studyassistant.core.navigation.Route.TaskScreen
import com.example.studyassistant.core.presentation.components.PermissionDialog
import com.example.studyassistant.core.presentation.components.PostNotificationPermissionTextProvider
import com.example.studyassistant.core.presentation.util.ObserveAsEvents
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.studytracker.presentation.dashboard.DashboardScreen
import com.example.studyassistant.studytracker.presentation.dashboard.DashboardScreenTopBar
import com.example.studyassistant.studytracker.presentation.dashboard.DashboardViewModel
import com.example.studyassistant.studytracker.presentation.session.SessionScreen
import com.example.studyassistant.studytracker.presentation.session.SessionScreenTopBar
import com.example.studyassistant.studytracker.presentation.session.SessionViewModel
import com.example.studyassistant.studytracker.presentation.session.StudySessionTimerService
import com.example.studyassistant.studytracker.presentation.subject.SubjectScreen
import com.example.studyassistant.studytracker.presentation.subject.SubjectScreenTopBar
import com.example.studyassistant.studytracker.presentation.subject.SubjectViewModel
import com.example.studyassistant.studytracker.presentation.task.TaskAction
import com.example.studyassistant.studytracker.presentation.task.TaskScreen
import com.example.studyassistant.studytracker.presentation.task.TaskScreenTopBar
import com.example.studyassistant.studytracker.presentation.task.TaskViewModel
import com.example.studyassistant.studytracker.presentation.util.Constants.DEEPLINK_DOMAIN
import com.example.studyassistant.ui.theme.StudyAssistantTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    private val permissionsToRequest = arrayOf(
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            Manifest.permission.POST_NOTIFICATIONS
        }else ""
    )

    private var isBound by mutableStateOf(false)
    private lateinit var timerService: StudySessionTimerService
    private val connection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?,
        ) {
            val binder = service as StudySessionTimerService.StudySessionTimerBinder
            timerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also {intent ->
            bindService(intent, connection, BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if(isBound){
                StudyAssistantTheme {
                    var isPostNotificationPermissionGranted = remember {
                        mutableStateOf <Boolean> (
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                isPermissionGranted(
                                    context = this@MainActivity,
                                    permission = Manifest.permission.POST_NOTIFICATIONS
                                )
                            } else true
                        )
                    }

                    var topBarContent by remember {
                        mutableStateOf <@Composable () -> Unit> ({ })
                    }
                    // Workaround to set null for FAB
                    var fabContent by remember {
                        mutableStateOf <@Composable () -> Unit> ({ })
                    }
                    var scaffoldModifier by remember {
                        mutableStateOf<Modifier>(Modifier)
                    }

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
                            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
                            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
                            modifier = Modifier.padding(innerPadding)
                        ) {

                            composable<DashboardScreen> {
                                val viewModel: DashboardViewModel = hiltViewModel()
                                val state by viewModel.state.collectAsStateWithLifecycle()
                                val tasks by viewModel.tasks.collectAsStateWithLifecycle()
                                val recentSessions by viewModel.recentSessions.collectAsStateWithLifecycle()

                                topBarContent = { DashboardScreenTopBar() }
                                fabContent = { }
                                scaffoldModifier = (Modifier)

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

                                var isEditSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
                                var isDeleteSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
                                val listState = rememberLazyListState()
                                // Manage the FAB state in the parent
                                var isFABExpanded by remember { mutableStateOf(true) }
                                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
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
                                                    subjectId = state.currentSubjectId
                                                )
                                            )
                                        },
                                        icon = { Icon(Icons.Default.Add, contentDescription = "Add Task") },
                                        text = { Text("Add Task") },
                                        expanded = isFABExpanded
                                    )
                                }
                                scaffoldModifier = (Modifier.nestedScroll(scrollBehavior.nestedScrollConnection))

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
                                        onCheckBoxClick = { viewModel.onAction(TaskAction.OnIsCompleteChange)}
                                    )
                                }
                                fabContent = { }
                                scaffoldModifier = (Modifier)
                                TaskScreen(
                                    state = state,
                                    isDeleteDialogOpen = isDeleteDialogOpen,
                                    onDeleteDialogVisibleChange = { isDeleteDialogOpen = it },
                                    onAction = viewModel::onAction
                                )
                            }
                            composable<SessionScreen>(
                                deepLinks = listOf(
                                    navDeepLink {
                                        uriPattern = "$DEEPLINK_DOMAIN://dashboard/session"
                                    }
                                )
                            ) {
                                val viewModel: SessionViewModel = hiltViewModel()
                                val state by viewModel.state.collectAsStateWithLifecycle()
                                val dialogQueue = viewModel.visiblePermissionDialogQueue

                                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                                    onResult = { perms ->
                                        permissionsToRequest.forEach { permission ->
                                            viewModel.onPermissionResult(
                                                permission = permission,
                                                isGranted = perms[permission] == true
                                            )
                                        }
                                    }
                                )

                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                    dialogQueue
                                        .reversed()
                                        .forEach { permission ->
                                            PermissionDialog(
                                                permissionTextProvider = when (permission) {
                                                    Manifest.permission.POST_NOTIFICATIONS -> {
                                                        PostNotificationPermissionTextProvider()
                                                    }
                                                    else -> return@forEach
                                                },
                                                isPermanentlyDeclined =
                                                !shouldShowRequestPermissionRationale(permission),
                                                onDismiss = viewModel::dismissDialog,
                                                onOkClick = {
                                                    viewModel.dismissDialog()
                                                    multiplePermissionResultLauncher.launch(
                                                        arrayOf(permission)
                                                    )
                                                },
                                                onGoToAppSettingsClick = ::openAppSettings
                                            )
                                        }
                                }
                                topBarContent = {
                                    SessionScreenTopBar(
                                        onBackButtonClicked = { navController.navigateUp() }
                                    )
                                }
                                fabContent = { }
                                scaffoldModifier = (Modifier)

                                SessionScreen(
                                    state = state,
                                    isPostNotificationGranted = isPostNotificationPermissionGranted.value,
                                    onAction =  viewModel::onAction,
                                    onPermissionTimerClick = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                                            && !isPostNotificationPermissionGranted.value) {
                                            multiplePermissionResultLauncher.launch(
                                                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                                            )
                                        }
                                    },
                                    timerService = timerService
                                )
                            }
                        }
                    }
                }
            }
        }
    }



    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}
