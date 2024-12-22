package com.example.studyassistant.navigation.graph.authentication.route

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import kotlin.collections.component1
import kotlin.collections.component2

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
    var changedText by remember {
        mutableStateOf("")
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
                    changedText =
                    event.changedMap.entries.joinToString(separator = "\n") { (key, value) ->
                        "$key Database has $value changes."
                    }
                    Log.d("SyncDismiss", "SyncChange: $changedText")
                }
            }
        }
    }

    Log.d("SyncDismiss", "SyncChangeOutside: $changedText")
    LoginScreen(
        state = state,
        changedText = changedText,
        onChangedTextClear = { changedText = "" },
        onAction = onAction
    )
}
