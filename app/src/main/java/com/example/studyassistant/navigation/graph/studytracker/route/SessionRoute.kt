package com.example.studyassistant.navigation.graph.studytracker.route

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.core.presentation.components.PermissionDialog
import com.example.studyassistant.core.presentation.components.PostNotificationPermissionTextProvider
import com.example.studyassistant.feature.studytracker.presentation.session.SessionScreen
import com.example.studyassistant.feature.studytracker.presentation.session.SessionScreenTopBar
import com.example.studyassistant.feature.studytracker.presentation.session.SessionViewModel
import com.example.studyassistant.feature.studytracker.presentation.session.StudySessionTimerService

@Composable
fun NavGraphBuilder.SessionRoute(
    permissionsToRequest: Array<String>,
    isPostNotificationPermissionGranted: Boolean,
    onPostNotificationPermissionGranted: (Boolean) -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    timerService: StudySessionTimerService,
    modifier: Modifier = Modifier
) {
    val viewModel: SessionViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dialogQueue = viewModel.visiblePermissionDialogQueue

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                when(permission){
                    Manifest.permission.POST_NOTIFICATIONS -> {
                        val isGranted = perms[permission] == true
                        onPostNotificationPermissionGranted(isGranted)
                        viewModel.onPermissionResult(
                            permission = permission,
                            isGranted = isGranted
                        )
                    }
                    else -> {
                        viewModel.onPermissionResult(
                            permission = permission,
                            isGranted = perms[permission] == true
                        )
                    }
                }
            }
        }
    )

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
                isPermanentlyDeclined = !checkPermissionRationale(permission),
                onDismiss = viewModel::dismissDialog,
                onOkClick = {
                    viewModel.dismissDialog()
                    multiplePermissionResultLauncher.launch(
                        arrayOf(permission)
                    )
                },
                onGoToAppSettingsClick = { onGoToAppSettingsClick() }
            )
        }

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = {
                SessionScreenTopBar(
                    onBackButtonClicked = { navController.navigateUp() }
                )
            },
            fabContent = { },
            scaffoldModifier = Modifier
        ))
    }

    SessionScreen(
        state = state,
        isPostNotificationGranted = isPostNotificationPermissionGranted,
        onAction =  viewModel::onAction,
        onPermissionTimerClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !isPostNotificationPermissionGranted) {
                multiplePermissionResultLauncher.launch(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                )
            }else onPostNotificationPermissionGranted(true)
        },
        timerService = timerService
    )
}

private fun Context.getActivity(): Activity? = this as? Activity

@Composable
private fun checkPermissionRationale(permission: String): Boolean {
    val context = LocalContext.current
    val activity = context.getActivity()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null) {
        activity.shouldShowRequestPermissionRationale(permission)
    } else {
        false
    }
}