package com.example.studyassistant.navigation.graph.studytracker

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.feature.studytracker.presentation.util.Constants.DEEPLINK_DOMAIN
import com.example.studyassistant.navigation.graph.studytracker.route.DashboardRoute
import com.example.studyassistant.navigation.graph.studytracker.route.SessionRoute
import com.example.studyassistant.navigation.graph.studytracker.route.SubjectRoute
import com.example.studyassistant.navigation.graph.studytracker.route.TaskRoute
import com.example.studyassistant.openAppSettings

//fun NavGraphBuilder.StudyTracker(){
//    navigation<Route.StudyTracker>(startDestination = Route.DashboardScreen) {
//        composable<Route.DashboardScreen> {
//            DashboardRoute(
//                selectedItemIndex = selectedItemIndex,
//                onSelectedItemIndexChange = { selectedItemIndex = it },
//                updateScaffold = { scaffoldState = it },
//                navController = navController
//            )
//        }
//        composable<Route.SubjectScreen> {
//            SubjectRoute(
//                updateScaffold = { scaffoldState = it },
//                subjectIdOnTimerService = timerService.subjectId.value,
//                navController = navController
//            )
//        }
//        composable<Route.TaskScreen> {
//            TaskRoute(
//                updateScaffold = { scaffoldState = it },
//                navController = navController
//            )
//        }
//        composable<Route.SessionScreen>(
//            deepLinks = listOf(
//                navDeepLink {
//                    uriPattern = "$DEEPLINK_DOMAIN://dashboard/session"
//                }
//            )
//        ) {
//            SessionRoute(
//                permissionsToRequest = permissionsToRequest,
//                isPostNotificationPermissionGranted =
//                isPostNotificationPermissionGranted.value,
//                onPostNotificationPermissionGranted = {
//                    isPostNotificationPermissionGranted.value = it
//                },
//                onGoToAppSettingsClick = ::openAppSettings,
//                updateScaffold = { scaffoldState = it },
//                navController = navController,
//                timerService = timerService
//            )
//        }
//    }
//}