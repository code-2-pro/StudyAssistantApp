@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.studytracker.presentation.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyassistant.studytracker.presentation.components.AddSubjectDialog
import com.example.studyassistant.studytracker.presentation.components.CountCard
import com.example.studyassistant.studytracker.presentation.components.DeleteDialog
import com.example.studyassistant.studytracker.presentation.components.studySessionList
import com.example.studyassistant.studytracker.presentation.components.taskList

@Composable
fun SubjectScreen(
    state: SubjectState,
    listState: LazyListState,
    onListScrolled: (firstVisibleItemIndex: Int) -> Unit,
    isEditSubjectDialogOpen: Boolean,
    isDeleteSubjectDialogOpen: Boolean,
    onEditSubjectDialogVisibleChange: (Boolean) -> Unit,
    onDeleteSubjectDialogVisibleChange: (Boolean) -> Unit,
    onAction: (SubjectAction) -> Unit,
    onBackButtonClick: () -> Unit,
    onTaskCardClick: (Int?) -> Unit
) {

    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }
    var previousName by remember { mutableStateOf<String>("") }
    var previousGoalHours by remember { mutableStateOf<String>("") }
    var previousColors by remember { mutableStateOf<List<Color>> (emptyList()) }

    LaunchedEffect(isEditSubjectDialogOpen) {
        if(isEditSubjectDialogOpen){
            previousName = state.subjectName
            previousGoalHours = state.goalStudyHours
            previousColors = state.subjectCardColors
        }else{
            previousName = ""
            previousGoalHours = ""
            previousColors = emptyList()
        }
    }

    // Observe the listState in the child
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { firstVisibleItemIndex ->
                // Notify the parent of the scroll position
                onListScrolled(firstVisibleItemIndex)
            }
    }

    AddSubjectDialog(
        isOpen = isEditSubjectDialogOpen,
        onDismissRequest = {
            onAction(SubjectAction.OnCancelSubjectChanges(
                previousName = previousName,
                previousGoalStudyHours = previousGoalHours,
                previousColor = previousColors
            ))
            onEditSubjectDialogVisibleChange(false)
        },
        onConfirmationButtonClick = {
            onAction(SubjectAction.UpdateSubject)
            onEditSubjectDialogVisibleChange(false)
        },
        selectedColors = state.subjectCardColors,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onColorChange = { onAction(SubjectAction.OnSubjectCardColorChange(it)) },
        onSubjectNameChange = { onAction(SubjectAction.OnSubjectNameChange(it)) },
        onGoalHoursChange = { onAction(SubjectAction.OnGoalStudyHoursChange(it)) },
    )

    DeleteDialog(
        title = "Delete Subject?",
        bodyText = "Are you sure, you want to delete this subject?" +
                " All the related tasks and study sessions will be permanently removed." +
                " This action can not be undone.",
        isOpen = isDeleteSubjectDialogOpen,
        onDismissRequest = { onDeleteSubjectDialogVisibleChange(false) },
        onConfirmationButtonClick = {
            onAction(SubjectAction.DeleteSubject)
            onDeleteSubjectDialogVisibleChange(false)
            onBackButtonClick()
        }
    )

    DeleteDialog(
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session?" +
                " Your studied hours will be reduced by this session time." +
                " This action can not be undone.",
        isOpen = isDeleteSessionDialogOpen,
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmationButtonClick = {
            onAction(SubjectAction.DeleteSession)
            isDeleteSessionDialogOpen = false
        }
    )
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        item{
            SubjectOverviewSection(
                studiedHours = state.studiedHours.toString(),
                goalHours = state.goalStudyHours,
                progress = state.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }
        taskList(
            sectionTitle = "UPCOMING TASKS",
            emptyListText = "You don't have any upcoming tasks.\n " +
                    "Click the + button to add new task.",
            tasks = state.upcomingTasks,
            onTaskCardClick = onTaskCardClick,
            onCheckBoxClick = { onAction(SubjectAction.OnTaskIsCompleteChange(it)) },
        )
        item{
            Spacer(modifier = Modifier.height(20.dp))
        }
        taskList(
            sectionTitle = "COMPLETED TASKS",
            emptyListText = "You don't have any completed tasks.\n " +
                    "Click the check box on completion of task.",
            tasks = state.completedTasks,
            onTaskCardClick = onTaskCardClick,
            onCheckBoxClick = { onAction(SubjectAction.OnTaskIsCompleteChange(it)) },
        )
        item{
            Spacer(modifier = Modifier.height(20.dp))
        }
        studySessionList(
            sectionTitle = "RECENT STUDY SESSIONS",
            emptyListText = "You don't have any recent study sessions.\n " +
                    "Start a study session to begin recording your progress.",
            sessions = state.recentSessions,
            onDeleteIconClick = {
                isDeleteSessionDialogOpen = true
                onAction(SubjectAction.OnDeleteSessionButtonClick(it))
            }
        )
    }

}

@Composable
fun SubjectScreenTopBar (
    title: String,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick =  onBackButtonClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "navigate back"
                )
            }
        },
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        actions = {
            IconButton(onClick = {onDeleteButtonClick()}) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Subject"
                )
            }
            IconButton(onClick = {onEditButtonClick()}) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Subject"
                )
            }
        }
    )
}

@Composable
private fun SubjectOverviewSection (
    studiedHours: String,
    goalHours: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val percentageProgress = remember(progress){
        (progress * 100).toInt().coerceIn(0 , 100)
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Study Hours",
            count = studiedHours,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = { 1f },
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = { progress },
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
            Text(text = "$percentageProgress%")
        }
    }
}