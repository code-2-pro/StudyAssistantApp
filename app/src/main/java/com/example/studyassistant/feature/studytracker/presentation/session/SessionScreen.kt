@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.feature.studytracker.presentation.session

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studyassistant.feature.studytracker.presentation.components.DeleteDialog
import com.example.studyassistant.feature.studytracker.presentation.components.SubjectListBottomSheet
import com.example.studyassistant.feature.studytracker.presentation.components.studySessionList
import com.example.studyassistant.feature.studytracker.presentation.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studyassistant.feature.studytracker.presentation.util.Constants.ACTION_SERVICE_START
import com.example.studyassistant.feature.studytracker.presentation.util.Constants.ACTION_SERVICE_STOP
import com.example.studyassistant.ui.theme.Red
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

@Composable
fun SessionScreen(
    state: SessionState,
    isPostNotificationGranted: Boolean,
    onAction: (SessionAction) -> Unit,
    onPermissionTimerClick: () -> Unit,
    timerService: StudySessionTimerService
) {

    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimerState

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    var isDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = state.subjects) {
        val subjectId = timerService.subjectId.value
        onAction(
            SessionAction.UpdateSubjectIdAndRelatedSubject(
                subjectId = subjectId,
                relatedToSubject = state.subjects.find { it.subjectId == subjectId }?.name
            )
        )
    }

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        onSubjectClicked = {subject ->
            scope.launch{ sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
            onAction(SessionAction.OnRelatedSubjectChange(subject))
        },
        onDismissRequest = { isBottomSheetOpen = false }
    )

    DeleteDialog(
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session? " +
                "This action can not be undone.",
        isOpen = isDeleteDialogOpen,
        onDismissRequest = { isDeleteDialogOpen = false },
        onConfirmationButtonClick = {
            onAction(SessionAction.DeleteSession)
            isDeleteDialogOpen = false
        },
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item{
            TimerSection(
                hours = hours,
                minutes = minutes,
                seconds = seconds,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }
        item{
            RelatedToSubjectSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                relatedToSubject = state.relatedToSubject ?: "",
                selectSubjectButtonClick = { isBottomSheetOpen = true },
                seconds = seconds
            )
        }
        item{
            ButtonsSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                startButtonClick = {
                    if(isPostNotificationGranted){
                        if(state.subjectId.isNotBlank() && state.relatedToSubject != null){
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = if(currentTimerState == TimerState.STARTED){
                                    ACTION_SERVICE_STOP
                                }else ACTION_SERVICE_START
                            )
                            timerService.subjectId.value = state.subjectId
                        }else{
                            onAction(SessionAction.NotifyToUpdateSubject)
                        }
                    }else{
                        onPermissionTimerClick()
                    }
                },
                cancelButtonClick = {
                    ServiceHelper.triggerForegroundService(
                        context = context,
                        action = ACTION_SERVICE_CANCEL
                    )
                    timerService.subjectId.value = null
                },
                finishButtonClick = {
                    val duration = timerService.duration.toLong(DurationUnit.SECONDS)
                    if(duration >= 36){
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = ACTION_SERVICE_CANCEL
                        )
                    }
                    timerService.subjectId.value = null
                    onAction(SessionAction.SaveSession(duration))
                },
                timerState = currentTimerState,
                seconds = seconds
            )
        }
        studySessionList(
            sectionTitle = "STUDY SESSIONS HISTORY",
            emptyListText = "You don't have any recent study sessions.\n " +
                    "Start a study session to begin recording your progress.",
            sessions = state.sessions,
            onDeleteIconClick = { session ->
                isDeleteDialogOpen = true
                onAction(SessionAction.OnDeleteSessionButtonClick(session))
            }
        )
    }

}

@Composable
fun SessionScreenTopBar (
    onBackButtonClicked: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate to Back Screen"
                )
            }
        },
        title = {
            Text(
                text = "Study Session",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    )
}

@Composable
private fun TimerSection (
    modifier: Modifier,
    hours: String,
    minutes: String,
    seconds: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )
        Row {
            AnimatedContent(
                targetState = hours,
                label = hours,
                transitionSpec = { timerTextAnimation() }
            ) { hours ->
                Text(
                    text = "$hours:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(
                targetState = minutes,
                label = minutes,
                transitionSpec = { timerTextAnimation() }
            ) { minutes ->
                Text(
                    text = "$minutes:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }

            AnimatedContent(
                targetState = seconds,
                label = seconds,
                transitionSpec = { timerTextAnimation() }
            ) { seconds ->
                Text(
                    text = seconds,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
        }
    }
}

@Composable
private fun RelatedToSubjectSection(
    relatedToSubject: String,
    selectSubjectButtonClick: () -> Unit,
    seconds: String,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Related to subject",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment =  Alignment.CenterVertically
        ) {
            Text(
                text = relatedToSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(
                onClick = selectSubjectButtonClick,
                enabled = seconds == "00"
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject"
                )
            }
        }
    }
}

@Composable
private fun ButtonsSection (
    modifier: Modifier,
    startButtonClick: () -> Unit,
    cancelButtonClick: () -> Unit,
    finishButtonClick: () -> Unit,
    timerState: TimerState,
    seconds: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = cancelButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                text = "Cancel"
            )
        }
        Button(
            onClick = startButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if(timerState == TimerState.STARTED) Red
                else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                text = when(timerState){
                    TimerState.STARTED -> "Stop"
                    TimerState.STOPPED -> "Resume"
                    else -> "Start"
                }
            )
        }
        Button(
            onClick = finishButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                text = "Finish"
            )
        }
    }
}

private fun timerTextAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(duration)) { fullHeight -> fullHeight } +
            fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically(animationSpec = tween(duration)) { fullHeight -> -fullHeight } +
            fadeOut(animationSpec = tween(duration))
}