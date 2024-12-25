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
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.example.studyassistant.core.navigation.NavigationAction
import com.example.studyassistant.core.navigation.Navigator
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.core.presentation.components.BottomBarNavigation
import com.example.studyassistant.core.presentation.util.ObserveAsEvents
import com.example.studyassistant.core.presentation.util.SnackbarController
import com.example.studyassistant.feature.authentication.presentation.AuthAction
import com.example.studyassistant.feature.authentication.presentation.AuthViewModel
import com.example.studyassistant.feature.studytracker.presentation.session.StudySessionTimerService
import com.example.studyassistant.feature.studytracker.presentation.util.Constants.DEEPLINK_DOMAIN
import com.example.studyassistant.navigation.graph.authentication.route.LoginRoute
import com.example.studyassistant.navigation.graph.authentication.route.RegisterRoute
import com.example.studyassistant.navigation.graph.flashcard.route.CategoryDetailRoute
import com.example.studyassistant.navigation.graph.flashcard.route.CategoryRoute
import com.example.studyassistant.navigation.graph.flashcard.route.FlashcardDisplayRoute
import com.example.studyassistant.navigation.graph.flashcard.route.FlashcardRoute
import com.example.studyassistant.navigation.graph.setting.MainSettingRoute
import com.example.studyassistant.navigation.graph.setting.route.AccountRoute
import com.example.studyassistant.navigation.graph.studytracker.route.DashboardRoute
import com.example.studyassistant.navigation.graph.studytracker.route.SessionRoute
import com.example.studyassistant.navigation.graph.studytracker.route.SubjectRoute
import com.example.studyassistant.navigation.graph.studytracker.route.TaskRoute
import com.example.studyassistant.navigation.graph.utillity.UtilityRoute
import com.example.studyassistant.navigation.graph.utillity.route.AssistantRoute
import com.example.studyassistant.ui.theme.StudyAssistantTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    private val permissionsToRequest = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        }
        else null
    ).toTypedArray()

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

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Use runBlocking to synchronously read the theme from DataStore
        val initialDarkTheme = runBlocking {
            dataStore.data
                .map { preferences ->
                    preferences[booleanPreferencesKey("user_dark_theme")]
                }
                .first()
        }

        setContent {
//            if(isBound){
            val isDarkTheme = initialDarkTheme?: isSystemInDarkTheme()
            // Determine the theme, falling back to system default if null
            var appliedDarkTheme by remember {
                mutableStateOf(isDarkTheme)
            }

                StudyAssistantTheme ( darkTheme = appliedDarkTheme){
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val authState by authViewModel.state.collectAsStateWithLifecycle()
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

                    var scaffoldComponentState by remember { mutableStateOf(ScaffoldComponentState()) }

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

                    var selectedItemIndex by rememberSaveable {
                        mutableIntStateOf(0)
                    }
                    val navController = rememberNavController()
                    Scaffold(
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        modifier = scaffoldComponentState.scaffoldModifier.fillMaxSize(),
                        topBar =  scaffoldComponentState.topBarContent,
                        floatingActionButton = scaffoldComponentState.fabContent,
                        bottomBar = {
                            BottomBarNavigation(
                                selectedItemIndex = selectedItemIndex,
                                onSelectedItemIndexChange = { selectedItemIndex = it },
                                navController = navController
                            )
                        }
                    ) { innerPadding ->
                        ObserveAsEvents(events = navigator.navigationActions) { action ->
                            val currentDestination = navController.currentBackStackEntry?.destination?.route
                            when(action) {
                                is NavigationAction.Navigate -> {
                                    if (currentDestination != action.route.toString()) {
                                        navController.navigate(
                                            action.route
                                        ) {
                                            action.navOptions(this)
                                        }
                                    }else return@ObserveAsEvents
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
                            navigation<Route.Authentication>(startDestination = Route.LoginScreen){
                                composable<Route.LoginScreen>{
                                    LoginRoute(
                                        state = authState,
                                        events = authViewModel.events,
                                        onAction = authViewModel::onAction,
                                        updateScaffold = {scaffoldComponentState = it}
                                    )
                                }
                                composable<Route.RegisterScreen>{
                                    RegisterRoute(
                                        state = authState,
                                        events = authViewModel.events,
                                        onAction = authViewModel::onAction,
                                        updateScaffold = {scaffoldComponentState = it}
                                    )
                                }
                            }
                            navigation<Route.StudyTracker>(startDestination = Route.DashboardScreen) {
                                composable<Route.DashboardScreen> {
                                    DashboardRoute(
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController
                                    )
                                }
                                composable<Route.SubjectScreen> {
                                    SubjectRoute(
                                        updateScaffold = { scaffoldComponentState = it },
                                        subjectIdOnTimerService = timerService.subjectId.value,
                                        navController = navController
                                    )
                                }
                                composable<Route.TaskScreen> {
                                    TaskRoute(
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController
                                    )
                                }
                                composable<Route.SessionScreen>(
                                    deepLinks = listOf(
                                        navDeepLink {
                                            uriPattern = "$DEEPLINK_DOMAIN://dashboard/session"
                                        }
                                    )
                                ) {
                                    SessionRoute(
                                        permissionsToRequest = permissionsToRequest,
                                        isPostNotificationPermissionGranted =
                                        isPostNotificationPermissionGranted.value,
                                        onPostNotificationPermissionGranted = {
                                            isPostNotificationPermissionGranted.value = it
                                        },
                                        onGoToAppSettingsClick = ::openAppSettings,
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController,
                                        timerService = timerService
                                    )
                                }
                            }
                            navigation<Route.Flashcard>(startDestination = Route.CategoryScreen) {
                                composable<Route.CategoryScreen> {
                                    CategoryRoute(
                                        updateScaffold = { scaffoldComponentState = it }
                                    )
                                }
                                composable<Route.CategoryDetailScreen> {
                                    CategoryDetailRoute(
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController
                                    )
                                }
                                composable<Route.FlashcardScreen> {
                                    FlashcardRoute(
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController
                                    )
                                }
                                composable<Route.FlashcardDisplayScreen> {
                                    FlashcardDisplayRoute(
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController
                                    )
                                }
                            }
                            navigation<Route.Utility>(startDestination = Route.UtilityScreen){
                                composable<Route.UtilityScreen>{
                                    UtilityRoute(
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController
                                    )
                                }
                                composable<Route.AssistantScreen>{
                                    AssistantRoute(
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController
                                    )
                                }
                            }
                            navigation<Route.Setting>(startDestination = Route.MainSettingScreen){
                                composable<Route.MainSettingScreen>{
                                    MainSettingRoute(
                                        state = authState,
                                        isDarkTheme = appliedDarkTheme,
                                        onDarkThemeToggle = {
                                            appliedDarkTheme = !appliedDarkTheme
                                            authViewModel.onAction(AuthAction.ToggleDarkTheme(appliedDarkTheme))
                                        },
                                        onLogoutClick = { selectedItemIndex = 0 },
                                        onAction = authViewModel::onAction,
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController
                                    )
                                }
                                composable<Route.AccountScreen>{
                                    AccountRoute(
                                        state = authState,
                                        events = authViewModel.events,
                                        onAction = authViewModel::onAction,
                                        updateScaffold = { scaffoldComponentState = it },
                                        navController = navController
                                    )
                                }
                            }

                        }
                    }
                }
//            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
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

