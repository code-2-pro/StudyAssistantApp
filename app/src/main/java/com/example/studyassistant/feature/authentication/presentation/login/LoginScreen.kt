package com.example.studyassistant.feature.authentication.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyassistant.feature.authentication.presentation.AuthAction
import com.example.studyassistant.feature.authentication.presentation.AuthState
import com.example.studyassistant.feature.authentication.presentation.login.componets.SyncOptionDialog
import com.example.studyassistant.feature.authentication.presentation.login.componets.UserChangeDialog
import com.example.studyassistant.ui.theme.StudyAssistantTheme

@Composable
fun LoginScreen(
    state: AuthState,
    changedMap: Map<String, Int>,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    val changedString = if (changedMap.isNotEmpty()) {
        changedMap.entries.joinToString(separator = "\n") { (key, value) ->
            "$key Database has $value changes."
        }
    } else ""

    var isSyncOptionDialogOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(changedString) {
        isSyncOptionDialogOpen = changedString.isNotBlank()
    }

    var isUserChangeDialogOpen by rememberSaveable { mutableStateOf(false) }

    SyncOptionDialog(
        text = changedString,
        isOpen = isSyncOptionDialogOpen,
        onDismiss = { isSyncOptionDialogOpen = false },
        onSendLocalDataClick = {
            onAction(AuthAction.OnSendDataToRemoteClick)
            isSyncOptionDialogOpen = false
        },
        onGetRemoteDataClick = {
            onAction(AuthAction.OnGetDataFromRemoteClick)
            isSyncOptionDialogOpen = false
        }
    )

    UserChangeDialog(
        isOpen = isUserChangeDialogOpen,
        userIsReplacedEmail = state.currentUser?.email ?: "",
        onDismiss = { isUserChangeDialogOpen = false },
        onUseNoAccountClick = {
            onAction(AuthAction.OnUseNoAccountClick)
            isUserChangeDialogOpen = false
        },
    )

    if(state.isLoading){
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }else{
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login Page",
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text(text = "Email")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text(text = "Password")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                enabled = !email.isBlank() && !password.isBlank(),
                onClick = {
                    onAction(AuthAction.OnLoginClick(email, password))
                }
            ) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                onAction(AuthAction.OnToRegisterPageClick)
            }) {
                Text(text = "Don't have an account, Register")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                if(state.currentUser != null){
                    isUserChangeDialogOpen = true
                }else{
                    onAction(AuthAction.OnUseNoAccountClick)
                }
            }) {
                Text(text = "Use without account (Data save on local)")
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginScreenPreview() {
    StudyAssistantTheme {
        LoginScreen(
            state = AuthState(),
            onAction = {},
            changedMap = emptyMap<String, Int>()
        )
    }
}