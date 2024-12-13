package com.example.studyassistant.feature.authentication.presentation.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.studyassistant.feature.authentication.presentation.AuthAction
import com.example.studyassistant.feature.authentication.presentation.AuthState
import com.example.studyassistant.feature.authentication.presentation.setting.components.RemoteDataDialog
import com.example.studyassistant.feature.authentication.presentation.setting.components.SettingList

@Composable
fun SettingsScreen(
    listState: LazyListState,
    state: AuthState,
    onLogoutClick:() -> Unit,
    onAction:(AuthAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRemoveDataDialogOpen by rememberSaveable { mutableStateOf(false) }
    RemoteDataDialog(
        isOpen = isRemoveDataDialogOpen,
        onDismiss = { isRemoveDataDialogOpen = false },
        onKeepLocalDataClick = {
            onAction(AuthAction.OnLogoutKeepLocalDataClick)
            isRemoveDataDialogOpen = false
        },
        onRemoveLocalDataClick = {
            onAction(AuthAction.OnLogoutRemoveLocalDataClick)
            isRemoveDataDialogOpen = false
        }
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        SettingList(
            listState = listState,
            onLogoutClick = {
                if(state.hasLocalData){
                    isRemoveDataDialogOpen = true
                }else{
                    onAction(AuthAction.OnLogoutKeepLocalDataClick)
                }
            }
        )
    }
}
