package com.example.studyassistant.feature.flashcard.domain.model

import com.example.studyassistant.ui.theme.gradient1
import com.example.studyassistant.ui.theme.gradient2
import com.example.studyassistant.ui.theme.gradient3
import com.example.studyassistant.ui.theme.gradient4
import com.example.studyassistant.ui.theme.gradient5

data class FlashcardCategory(
    val name: String,
    val isMeaningful: Boolean,
    val colors: List<Int>,
    val categoryId: String
)
{
    companion object{
        val categoryCardColors = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}
