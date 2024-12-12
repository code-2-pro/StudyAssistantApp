package com.example.studyassistant.feature.studytracker.domain.model

import com.example.studyassistant.ui.theme.gradient1
import com.example.studyassistant.ui.theme.gradient2
import com.example.studyassistant.ui.theme.gradient3
import com.example.studyassistant.ui.theme.gradient4
import com.example.studyassistant.ui.theme.gradient5

data class Subject(
    val name: String,
    val goalHours: Float,
    val colors: List<Int>,
    val subjectId: String
){
    companion object{
        val subjectCardColors = listOf(gradient1, gradient2, gradient3, gradient4, gradient5)
    }
}
