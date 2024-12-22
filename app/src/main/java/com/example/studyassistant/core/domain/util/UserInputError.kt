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
    enum class FlashcardCategoryNameError: UserInputError {
        IS_BLANK,
        TOO_SHORT,
        TOO_LONG
    }
    enum class FlashcardInputError: UserInputError {
        IS_BLANK,
        TOO_SHORT,
        TOO_LONG
    }

    enum class CardQuantityError: UserInputError {
        IS_BLANK,
        INVALID_NUMBER,
        AT_LEAST_ONE_CARD,
        MAX_IS_10_CARD
    }

}

