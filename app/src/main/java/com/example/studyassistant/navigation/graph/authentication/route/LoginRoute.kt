package com.example.studyassistant.navigation.graph.authentication.route

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import com.example.studyassistant.core.presentation.ScaffoldComponentState
import com.example.studyassistant.core.presentation.util.ObserveAsEvents
import com.example.studyassistant.core.presentation.util.toString
import com.example.studyassistant.feature.authentication.presentation.AuthAction
import com.example.studyassistant.feature.authentication.presentation.AuthEvent
import com.example.studyassistant.feature.authentication.presentation.AuthState
import com.example.studyassistant.feature.authentication.presentation.login.LoginScreen
import kotlinx.coroutines.flow.Flow

@Composable
fun NavGraphBuilder.LoginRoute(
    state: AuthState,
    events: Flow<AuthEvent>,
    onAction: (AuthAction) -> Unit,
    updateScaffold: (ScaffoldComponentState) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(key1 = true) {
        updateScaffold(
            ScaffoldComponentState(
                topBarContent = { },
                fabContent = { },
                scaffoldModifier = Modifier
            )
        )
    }

    // Use a mutable state for the map
    val changedMap = rememberSaveable {
        mutableStateOf<Map<String, Int>>(emptyMap())
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
            is AuthEvent.SyncError -> {
                Toast.makeText(
                    context,
                    event.error.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
            is AuthEvent.SyncChange -> {
                if (event.changedMap.isNotEmpty()) {
                    changedMap.value = event.changedMap
                }
            }
        }
    }

    LoginScreen(
        state = state,
        changedMap = changedMap.value, // Pass the actual map
        onAction = onAction
    )
}
