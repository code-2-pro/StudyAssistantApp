package com.example.studyassistant.domain.model

import androidx.compose.ui.graphics.Color
import com.example.studyassistant.presentation.theme.gradient1
import com.example.studyassistant.presentation.theme.gradient2
import com.example.studyassistant.presentation.theme.gradient3
import com.example.studyassistant.presentation.theme.gradient4
import com.example.studyassistant.presentation.theme.gradient5

data class Subject(
    val name: String,
    val goalHours: Float,
    val colors: List<Int>,
    val subjectId: Int? = null
){
    companion object{
        val subjectCardColors = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}
