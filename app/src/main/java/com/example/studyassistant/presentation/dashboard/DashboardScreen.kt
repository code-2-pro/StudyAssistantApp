@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.presentation.dashboard

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studyassistant.R
import com.example.studyassistant.domain.model.Subject
import com.example.studyassistant.presentation.components.AddSubjectDialog
import com.example.studyassistant.presentation.components.CountCard
import com.example.studyassistant.presentation.components.DeleteDialog
import com.example.studyassistant.presentation.components.SubjectCard
import com.example.studyassistant.presentation.components.studySessionList
import com.example.studyassistant.presentation.components.taskList
import com.example.studyassistant.sessions
import com.example.studyassistant.subjects
import com.example.studyassistant.tasks

@Composable
fun DashboardScreen(modifier: Modifier = Modifier) {

    var isAddSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var isDeleteSessionDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var subjectName by remember {
        mutableStateOf("")
    }
    var goalHours by remember {
        mutableStateOf("")
    }
    var selectedColor by remember {
        mutableStateOf(Subject.subjectCardColors.first())
    }

    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        onDismissRequest = { isAddSubjectDialogOpen = false },
        onConfirmationButtonClick = { isAddSubjectDialogOpen = false },
        selectedColors = selectedColor,
        subjectName = subjectName,
        goalHours = goalHours,
        onColorChange = { selectedColor = it },
        onSubjectNameChange = {subjectName = it},
        onGoalHoursChange = {goalHours = it},
    )

    DeleteDialog(
        title = "Delete Session?",
        bodyText = "Are you sure, you want to delete this session?" +
                " Your studied hours will be reduced by this session time." +
                " This action can not be undone.",
        isOpen = isDeleteSessionDialogOpen,
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmationButtonClick = { isDeleteSessionDialogOpen = false }
    )

    Scaffold (
        topBar = { DashboardScreenTopBar() }

    ) { paddingValue ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ) {
            item{
                CountCardSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = 5,
                    studiedHours = "10",
                    goalHours = "15"
                )
            }
            item{
                SubjectCardSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = subjects,
                    onAddIconClicked = {
                        isAddSubjectDialogOpen = true
                    }
                )
            }
            item{
                Button(
                    onClick = {},
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
                onTaskCardClick = { TODO() },
                onCheckBoxClick = { TODO() },
            )
            item{
                Spacer(modifier = Modifier.height(20.dp))
            }
            studySessionList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any recent study sessions.\n " +
                        "Start a study session to begin recording your progress.",
                sessions = sessions,
                onDeleteIconClick = { isDeleteSessionDialogOpen = true }
            )
        }
    }
}

@Composable
private fun DashboardScreenTopBar() {
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
                        gradientColors = subject.colors,
                        onClick = { TODO()}
                    )

                }
            }
    }
}

