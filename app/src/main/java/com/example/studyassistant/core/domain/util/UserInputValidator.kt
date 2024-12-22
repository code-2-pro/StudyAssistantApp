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

fun validateFlashcardCategoryName(categoryName: String): Result<Unit, UserInputError.FlashcardCategoryNameError> {
    if(categoryName.isBlank()) {
        return Result.Error(UserInputError.FlashcardCategoryNameError.IS_BLANK)
    }

    if(categoryName.length < 2) {
        return Result.Error(UserInputError.FlashcardCategoryNameError.TOO_SHORT)
    }

    if(categoryName.length > 20) {
        return Result.Error(UserInputError.FlashcardCategoryNameError.TOO_LONG)
    }

    return Result.Success(Unit)
}

fun validateFlashcardInput(inputString: String): Result<Unit, UserInputError.FlashcardInputError> {
    if(inputString.isBlank()) {
        return Result.Error(UserInputError.FlashcardInputError.IS_BLANK)
    }

    if(inputString.length < 2) {
        return Result.Error(UserInputError.FlashcardInputError.TOO_SHORT)
    }

    if(inputString.length > 500) {
        return Result.Error(UserInputError.FlashcardInputError.TOO_LONG)
    }

    return Result.Success(Unit)
}

fun validateCardQuantity(quantity: String): Result<Unit, UserInputError.CardQuantityError> {
    if(quantity.isBlank()) {
        return Result.Error(UserInputError.CardQuantityError.IS_BLANK)
    }

    if(quantity.toIntOrNull() == null) {
        return Result.Error(UserInputError.CardQuantityError.INVALID_NUMBER)
    }

    if(quantity.toInt() < 1) {
        return Result.Error(UserInputError.CardQuantityError.AT_LEAST_ONE_CARD)
    }

    if(quantity.toInt() > 10) {
        return Result.Error(UserInputError.CardQuantityError.MAX_IS_10_CARD)
    }

    return Result.Success(Unit)
}