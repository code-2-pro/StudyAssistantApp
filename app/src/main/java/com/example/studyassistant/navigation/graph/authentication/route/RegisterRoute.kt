package com.example.studyassistant.navigation.graph.authentication.route

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.core.presentation.util.ObserveAsEvents
import com.example.studyassistant.core.presentation.util.toString
import com.example.studyassistant.feature.authentication.presentation.AuthAction
import com.example.studyassistant.feature.authentication.presentation.AuthEvent
import com.example.studyassistant.feature.authentication.presentation.AuthState
import com.example.studyassistant.feature.authentication.presentation.register.RegisterScreen
import com.example.studyassistant.feature.studytracker.presentation.dashboard.DashboardScreenTopBar
import kotlinx.coroutines.flow.Flow

@Composable
fun NavGraphBuilder.RegisterRoute(
    state: AuthState,
    events: Flow<AuthEvent>,
    onAction:(AuthAction) -> Unit,
    updateScaffold: (ScaffoldComponentState) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(key1 = true) {
        updateScaffold(ScaffoldComponentState(
            topBarContent = { DashboardScreenTopBar() },
            fabContent = { },
            scaffoldModifier = Modifier
        ))
    }

    val context = LocalContext.current
    ObserveAsEvents(events = events) { event ->
        when (event) {
            is AuthEvent.AuthenticationError -> {
                Toast.makeText(
                    context,
                    event.error.toString(context),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> return@ObserveAsEvents
        }
    }

    RegisterScreen(
        state = state,
        onAction = onAction
    )
}