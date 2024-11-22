@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.studytracker.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studyassistant.R
import com.example.studyassistant.studytracker.domain.model.Session
import com.example.studyassistant.studytracker.domain.model.Subject
import com.example.studyassistant.studytracker.domain.model.Task
import com.example.studyassistant.studytracker.presentation.components.AddSubjectDialog
import com.example.studyassistant.studytracker.presentation.components.CountCard
import com.example.studyassistant.studytracker.presentation.components.DeleteDialog
import com.example.studyassistant.studytracker.presentation.components.SubjectCard
import com.example.studyassistant.studytracker.presentation.components.studySessionList
import com.example.studyassistant.studytracker.presentation.components.taskList

@Composable
fun DashboardScreen(
    state: DashboardState,
    tasks: List<Task>,
    recentSessions: List<Session>,
    onAction: (DashboardAction) -> Unit,
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int?) -> Unit,
    onStartSessionButtonClick: () -> Unit,
) {

    var isAddSubjectDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isDeleteSessionDialogOpen by rememberSaveable { mutableStateOf(false) }

    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmationButtonClick = {
            onAction(DashboardAction.SaveSubject)
            isAddSubjectDialogOpen = false
        },
        selectedColors = state.subjectCardColors,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onColorChange = { onAction(DashboardAction.OnSubjectCardColorChange(it)) },
        onSubjectNameChange = { onAction(DashboardAction.OnSubjectNameChange(it)) },
        onGoalHoursChange = { onAction(DashboardAction.OnGoalStudyHoursChange(it)) },
    )

    DeleteDialog(
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session?" +
                " Your studied hours will be reduced by this session time." +
                " This action can not be undone.",
        isOpen = isDeleteSessionDialogOpen,
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmationButtonClick = {
            onAction(DashboardAction.DeleteSession)
            isDeleteSessionDialogOpen = false
        }
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item{
            CountCardSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                subjectCount = state.totalSubjectCount,
                studiedHours = state.totalStudiedHours.toString(),
                goalHours = state.totalGoalStudyHours.toString()
            )
        }
        item{
            SubjectCardSection(
                modifier = Modifier.fillMaxWidth(),
                subjectList = state.subjects,
                onAddIconClicked = {
                    isAddSubjectDialogOpen = true
                },
                onSubjectCardClick = onSubjectCardClick
            )
        }
        item{
            Button(
                onClick = onStartSessionButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 20.dp)
            ) {
                Text(text = "Start Study Session")
            }
        }
        taskList(
            sectionTitle = "UPCOMING TASKS",
            emptyListText = "You don't have any upcoming tasks.\n " +
                    "Click the + button in subject screen to add new task.",
            tasks = tasks,
            onTaskCardClick = onTaskCardClick,
            onCheckBoxClick = { onAction(DashboardAction.OnTaskIsCompleteChange(it)) },
        )
        item{
            Spacer(modifier = Modifier.height(20.dp))
        }
        studySessionList(
            sectionTitle = "RECENT STUDY SESSIONS",
            emptyListText = "You don't have any recent study sessions.\n " +
                    "Start a study session to begin recording your progress.",
            sessions = recentSessions,
            onDeleteIconClick = {
                onAction(DashboardAction.OnDeleteSessionButtonClick(it))
                isDeleteSessionDialogOpen = true
            }
        )
    }

}

@Composable
fun DashboardScreenTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Study Assistant",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    )
}

@Composable
private fun CountCardSection (
    modifier: Modifier = Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalHours: String
) {
    Row(
        modifier = modifier
    ){
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Subject Count",
            count = "$subjectCount"
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hour",
            count = studiedHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hour",
            count = goalHours
        )
    }
}

@Composable
private fun SubjectCardSection(
    subjectList: List<Subject>,
    emptyListText: String =
        "You don't have any subjects.\n Click the + button to add new subject.",
    onAddIconClicked: () -> Unit,
    onSubjectCardClick: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = { onAddIconClicked() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject"
                )
            }
        }
        if(subjectList.isEmpty()){
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.img_books),
                contentDescription = emptyListText
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ){
            items(subjectList){ subject ->
                SubjectCard(
                    subjectName = subject.name,
                    gradientColors = subject.colors.map { Color(it) },
                    onClick = { onSubjectCardClick(subject.subjectId) }
                )

            }
        }
    }
}

