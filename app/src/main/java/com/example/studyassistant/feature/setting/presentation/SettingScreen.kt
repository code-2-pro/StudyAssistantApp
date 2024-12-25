package com.example.studyassistant.feature.setting.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studyassistant.feature.authentication.presentation.AuthAction
import com.example.studyassistant.feature.authentication.presentation.AuthState
import com.example.studyassistant.feature.setting.presentation.components.ProfileCard
import com.example.studyassistant.feature.setting.presentation.components.RemoteDataDialog
import com.example.studyassistant.feature.setting.presentation.components.SettingList

@Composable
fun SettingsScreen(
    listState: LazyListState,
    state: AuthState,
    isDarkTheme: Boolean,
    onDarkThemeToggle: () -> Unit,
    onLogoutClick: () -> Unit,
    onAccountClick: () -> Unit,
    onAction:(AuthAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRemoveDataDialogOpen by rememberSaveable { mutableStateOf(false) }
    RemoteDataDialog(
        isOpen = isRemoveDataDialogOpen,
        onDismiss = { isRemoveDataDialogOpen = false },
        onKeepLocalDataClick = {
            onAction(AuthAction.LogoutKeepLocalData)
            isRemoveDataDialogOpen = false
            onLogoutClick()
        },
        onRemoveLocalDataClick = {
            onAction(AuthAction.LogoutRemoveLocalData)
            isRemoveDataDialogOpen = false
            onLogoutClick()
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileCard(
                displayName = state.currentUser?.displayName ?: "",
                hasAccount = state.currentUser != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingList(
                state = state,
                listState = listState,
                isDarkTheme = isDarkTheme,
                onDarkThemeToggle = {
                    onDarkThemeToggle()
                },
                onAccountClick = onAccountClick,
                onLogoutClick = {
                    if (state.hasLocalData) {
                        isRemoveDataDialogOpen = true
                    } else {
                        onAction(AuthAction.LogoutKeepLocalData)
                        onLogoutClick()
                    }
                }
            )
        }
    }
}
