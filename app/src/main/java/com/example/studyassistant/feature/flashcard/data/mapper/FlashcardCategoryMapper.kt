package com.example.studyassistant.feature.flashcard.data.mapper

import com.example.studyassistant.feature.authentication.data.dto.RemoteFlashcardCategory
import com.example.studyassistant.feature.flashcard.data.local.entity.FlashcardCategoryEntity
import com.example.studyassistant.feature.flashcard.domain.model.FlashcardCategory

fun FlashcardCategory.toFlashcardCategoryEntity(): FlashcardCategoryEntity {
    return FlashcardCategoryEntity(
        name = name,
        isMeaningful = isMeaningful,
        colors = colors,
        categoryId = categoryId
    )
}

fun FlashcardCategoryEntity.toFlashcardCategory(): FlashcardCategory {
    return FlashcardCategory(
        name = name,
        isMeaningful = isMeaningful,
        colors = colors,
        categoryId = categoryId
    )
}

fun FlashcardCategory.toRemoteFlashcardCategory(userId: String): RemoteFlashcardCategory{
    return RemoteFlashcardCategory(
        userId = userId,
        name = name,
        isMeaningful = isMeaningful,
        colors = colors,
        categoryId = categoryId
    )
}

fun RemoteFlashcardCategory.toFlashcardCategory(): FlashcardCategory{
    return FlashcardCategory(
        name = name,
        isMeaningful = isMeaningful,
        colors = colors,
        categoryId = categoryId
    )
}