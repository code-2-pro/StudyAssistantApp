package com.example.studyassistant.navigation.graph.authentication.route

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
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.core.presentation.components.BottomBarNavigation
import com.example.studyassistant.feature.authentication.presentation.AuthAction
import com.example.studyassistant.feature.authentication.presentation.AuthState
import com.example.studyassistant.feature.authentication.presentation.setting.SettingsScreen
import com.example.studyassistant.feature.authentication.presentation.setting.components.SettingScreenTopBar

@Composable
fun NavGraphBuilder.MainSettingRoute(
    selectedItemIndex: Int,
    onSelectedItemIndexChange :(Int) -> Unit,
    state: AuthState,
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

    SettingsScreen(
        onAction = onAction,
        listState = listState
    )


}