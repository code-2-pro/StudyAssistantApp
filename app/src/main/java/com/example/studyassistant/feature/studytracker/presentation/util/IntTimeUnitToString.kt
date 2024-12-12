package com.example.studyassistant.feature.studytracker.presentation.util

fun Int.pad(): String {
    return this.toString().padStart(length = 2, padChar = '0')
}