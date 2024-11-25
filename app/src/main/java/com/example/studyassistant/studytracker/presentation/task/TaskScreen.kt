@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.studytracker.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.core.domain.util.validateTaskTitle
import com.example.studyassistant.core.presentation.util.toString
import com.example.studyassistant.studytracker.presentation.components.DeleteDialog
import com.example.studyassistant.studytracker.presentation.components.SubjectListBottomSheet
import com.example.studyassistant.studytracker.presentation.components.TaskCheckBox
import com.example.studyassistant.studytracker.presentation.components.TaskDatePicker
import com.example.studyassistant.studytracker.presentation.mapper.changeMillisToDateString
import com.example.studyassistant.studytracker.presentation.util.CurrentOrFutureSelectableDates
import com.example.studyassistant.studytracker.presentation.util.Priority
import kotlinx.coroutines.launch

@Composable
fun TaskScreen(
    state: TaskState,
    isDeleteDialogOpen: Boolean,
    onDeleteDialogVisibleChange: (Boolean) -> Unit,
    onAction: (TaskAction) -> Unit
) {

    var isDatePickerDialogOpen by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = CurrentOrFutureSelectableDates
    )

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    var taskTitleError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current
    val taskTitleResult = validateTaskTitle(state.title)

    taskTitleError = when (taskTitleResult) {
        is Result.Error -> taskTitleResult.error.toString(context)
        is Result.Success -> null
    }

    DeleteDialog(
        title = "Delete Task?",
        bodyText = "Are you sure, you want to delete this task? " +
                "This action can not be undone.",
        isOpen = isDeleteDialogOpen,
        onDismissRequest = { onDeleteDialogVisibleChange(false) },
        onConfirmationButtonClick = {
            onAction(TaskAction.DeleteTask)
            onDeleteDialogVisibleChange(false)
        },
    )

    TaskDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = {
            isDatePickerDialogOpen = false
        },
        onConfirmButtonClicked = {
            onAction(TaskAction.OnDateChange(millis = datePickerState.selectedDateMillis))
            isDatePickerDialogOpen = false
        }
    )

    SubjectListBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subjects = state.subjects,
        onSubjectClicked = {subject ->
            scope.launch{ sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
            onAction(TaskAction.OnRelatedSubjectSelect(subject))
        },
        onDismissRequest = { isBottomSheetOpen = false }
    )

    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.title,
            onValueChange = { onAction(TaskAction.OnTitleChange(it)) },
            label = {Text(text = "Title")},
            singleLine = true,
            isError = taskTitleError != null && state.title.isNotBlank(),
            supportingText = {Text(text = taskTitleError.orEmpty() )}
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.description,
            onValueChange = { onAction(TaskAction.OnDescriptionChange(it)) },
            label = {Text(text = "Description")}
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Due Date",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment =  Alignment.CenterVertically
        ) {
            Text(
                text = state.dueDate.changeMillisToDateString(),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = { isDatePickerDialogOpen = true }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Due Date"
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Priority",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Priority.entries.forEach{ priority ->
                PriorityButton(
                    modifier = Modifier.weight(1f),
                    label = priority.title,
                    backgroundColor = priority.color,
                    borderColor = if(priority == state.priority){
                        Color.White
                    }else Color.Transparent,
                    labelColor = if (priority == state.priority){
                        Color.White
                    }else Color.White.copy(alpha = 0.7f),
                    onClick = { onAction(TaskAction.OnPriorityChange(priority)) }
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Related to subject",
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment =  Alignment.CenterVertically
        ) {
            val firstSubject = state.subjects.firstOrNull()?.name ?: ""
            Text(
                text = state.relatedToSubject ?: firstSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = { isBottomSheetOpen = true }) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject"
                )
            }
        }
        Button(
            enabled = taskTitleError == null,
            onClick = { onAction(TaskAction.SaveTask) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Text(text = "Save")
        }
    }

}


@Composable
fun TaskScreenTopBar(
    isTaskExist: Boolean,
    isComplete: Boolean,
    checkBoxBorderColor: Color,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onCheckBoxClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        title = {
            Text(
                text = "Task",
                style = MaterialTheme.typography.headlineSmall
            )},
        actions = {
            if(isTaskExist){
                TaskCheckBox(
                    isComplete = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick = onCheckBoxClick
                )
                IconButton(onClick = onDeleteButtonClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Task"
                    )
                }
            }
        }
    )
}

@Composable
private fun PriorityButton (
    label: String,
    backgroundColor: Color,
    borderColor: Color,
    labelColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(5.dp)
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = label,
            color = labelColor
        )
    }
}