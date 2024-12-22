package com.example.studyassistant.navigation.graph.setting

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.navigation.Route
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.feature.authentication.presentation.AuthAction
import com.example.studyassistant.feature.authentication.presentation.AuthState
import com.example.studyassistant.feature.setting.presentation.SettingsScreen
import com.example.studyassistant.feature.setting.presentation.components.SettingScreenTopBar

@Composable
fun NavGraphBuilder.MainSettingRoute(
    state: AuthState,
    isDarkTheme: Boolean,
    onDarkThemeToggle: () -> Unit,
    onLogoutClick :() -> Unit,
    onAction:(AuthAction) -> Unit,
    updateScaffold: (ScaffoldComponentState) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val hasScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemScrollOffset > 0
        }
    }
    val appBarElevation by animateDpAsState(
        targetValue = if (hasScrolled) 4.dp else 0.dp,
        label = "settingAppBarElevation"
    )

    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = {
                SettingScreenTopBar(
                    appBarElevation = appBarElevation,
                    hasScrolled = hasScrolled
                )
            },
            fabContent = { },
            scaffoldModifier = Modifier
        ))
    }

    SettingsScreen(
        isDarkTheme = isDarkTheme,
        onDarkThemeToggle = onDarkThemeToggle,
        onAccountClick = {
            navController.navigate(Route.AccountScreen)
        },
        onLogoutClick = onLogoutClick,
        onAction = onAction,
        state = state,
        listState = listState,
    )


}