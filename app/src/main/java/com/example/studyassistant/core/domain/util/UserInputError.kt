package com.example.studyassistant.core.domain.util

sealed interface UserInputError: Error {
    enum class SubjectNameError: UserInputError {
        IS_BLANK,
        TOO_SHORT,
        TOO_LONG
    }
    enum class GoalHoursError: UserInputError {
        IS_BLANK,
        INVALID_NUMBER,
        AT_LEAST_ONE_HOUR,
        MAX_IS_1000_HOURS
    }
    enum class TaskTitleError: UserInputError {
        IS_BLANK,
        TOO_SHORT,
        TOO_LONG
    }
}