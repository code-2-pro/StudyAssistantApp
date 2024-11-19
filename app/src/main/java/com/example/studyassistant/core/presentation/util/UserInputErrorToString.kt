package com.example.studyassistant.core.presentation.util

import android.content.Context
import com.example.studyassistant.R
import com.example.studyassistant.core.domain.util.UserInputError
import com.example.studyassistant.core.domain.util.UserInputError.GoalHoursError
import com.example.studyassistant.core.domain.util.UserInputError.SubjectNameError
import com.example.studyassistant.core.domain.util.UserInputError.TaskTitleError

fun SubjectNameError.toString(context: Context): String{
    val resId = when(this){
        SubjectNameError.IS_BLANK -> R.string.error_subject_name_is_blank
        SubjectNameError.TOO_SHORT -> R.string.error_subject_name_too_short
        SubjectNameError.TOO_LONG -> R.string.error_subject_name_too_long
    }
    return context.getString(resId)
}

fun GoalHoursError.toString(context: Context): String{
    val resId = when(this){
        GoalHoursError.IS_BLANK -> R.string.error_goal_hours_is_blank
        GoalHoursError.INVALID_NUMBER -> R.string.error_goal_hours_is_invalid
        GoalHoursError.AT_LEAST_ONE_HOUR -> R.string.error_goal_hours_is_at_least_one_hour
        GoalHoursError.MAX_IS_1000_HOURS -> R.string.error_goal_hours_is_max_at_1000
    }
    return context.getString(resId)
}

fun TaskTitleError.toString(context: Context): String{
    val resId = when(this){
        TaskTitleError.IS_BLANK -> R.string.error_task_title_is_blank
        TaskTitleError.TOO_SHORT -> R.string.error_task_title_too_short
        TaskTitleError.TOO_LONG -> R.string.error_task_title_too_long
    }
    return context.getString(resId)
}