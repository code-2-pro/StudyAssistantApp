package com.example.studyassistant.core.domain.util

fun validateSubjectName(subjectName: String): Result<Unit, UserInputError.SubjectNameError> {
    if(subjectName.isBlank()) {
        return Result.Error(UserInputError.SubjectNameError.IS_BLANK)
    }

    if(subjectName.length < 2) {
        return Result.Error(UserInputError.SubjectNameError.TOO_SHORT)
    }

    if(subjectName.length > 20) {
        return Result.Error(UserInputError.SubjectNameError.TOO_LONG)
    }

    return Result.Success(Unit)
}

fun validateGoalHours(goalHours: String): Result<Unit, UserInputError.GoalHoursError> {
    if(goalHours.isBlank()) {
        return Result.Error(UserInputError.GoalHoursError.IS_BLANK)
    }

    if(goalHours.toFloatOrNull() == null) {
        return Result.Error(UserInputError.GoalHoursError.INVALID_NUMBER)
    }

    if(goalHours.toFloat() < 1f) {
        return Result.Error(UserInputError.GoalHoursError.AT_LEAST_ONE_HOUR)
    }

    if(goalHours.toFloat() > 1000f) {
        return Result.Error(UserInputError.GoalHoursError.MAX_IS_1000_HOURS)
    }

    return Result.Success(Unit)
}

fun validateTaskTitle(taskTitle: String): Result<Unit, UserInputError.TaskTitleError> {
    if(taskTitle.isBlank()) {
        return Result.Error(UserInputError.TaskTitleError.IS_BLANK)
    }

    if(taskTitle.length < 4) {
        return Result.Error(UserInputError.TaskTitleError.TOO_SHORT)
    }

    if(taskTitle.length > 30) {
        return Result.Error(UserInputError.TaskTitleError.TOO_LONG)
    }

    return Result.Success(Unit)
}