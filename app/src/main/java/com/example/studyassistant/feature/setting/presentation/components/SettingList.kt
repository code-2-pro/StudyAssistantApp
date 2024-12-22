package com.example.studyassistant.feature.setting.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.studyassistant.R
import com.example.studyassistant.feature.authentication.presentation.AuthState

@Composable
fun SettingList(
    state: AuthState,
    listState: LazyListState,
    isDarkTheme: Boolean,
    onDarkThemeToggle: () -> Unit,
    onLogoutClick: () -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.widthIn(max = 600.dp),
        state = listState
    ) {
        if(state.currentUser != null){
            item {
                SettingItem(
                    title = "Account",
                    icon = Icons.Outlined.AccountCircle,
                    onClick = { onAccountClick() }
                )
            }
        }
        item {
            SettingItem(
                title = "Notifications",
                icon = Icons.Outlined.Notifications,
                onClick = { /*TODO*/ })
        }

        item {
            SwitchButton(
                title = "Dark Theme",
                icon = painterResource(R.drawable.baseline_dark_mode_24),
                isDarkTheme = isDarkTheme,
                onToggleChange = onDarkThemeToggle
            )
        }

        item { HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp)) }

        item {
            SettingItem(
                title = "Send Feedback",
                icon = Icons.Outlined.Email,
                onClick = { /*TODO*/ })
        }
        item {
            SettingItem(
                title = "Review App",
                icon = Icons.Outlined.Star,
                onClick = { /*TODO*/ })
        }

        item { HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp)) }

        item {
            SettingItem(
                title = "Log Out",
                icon = Icons.AutoMirrored.Outlined.ExitToApp,
                onClick = { onLogoutClick() })
        }

        item { HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp)) }

        item {
            AppVersion(
                versionText = "Version 1.0.0",
                copyrights = "© 2024 Study Assistant",
            )
        }
    }
}

