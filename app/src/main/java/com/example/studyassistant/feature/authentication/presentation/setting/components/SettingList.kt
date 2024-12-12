package com.example.studyassistant.feature.authentication.presentation.setting.components

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
import androidx.compose.ui.unit.dp

@Composable
fun SettingList(
    listState: LazyListState,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.widthIn(max = 600.dp),
        state = listState
    ) {
        item {
            SettingItem(
                title = "Account",
                icon = Icons.Outlined.AccountCircle,
                onClick = { /*TODO*/ })
        }
        item {
            SettingItem(
                title = "Notifications",
                icon = Icons.Outlined.Notifications,
                onClick = { /*TODO*/ })
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

