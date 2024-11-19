package com.example.studyassistant.studytracker.presentation.mapper

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long?.changeMillisToDateString(): String{
    val date : LocalDate = this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MM yyyy"))
}

fun Long.toHours(): Float{
    val hours = this.toFloat() / 3600f
    return "%.2f".format(hours).toFloat()
}