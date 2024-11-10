@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.studyassistant.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.ui.graphics.Color
import com.example.studyassistant.presentation.theme.Green
import com.example.studyassistant.presentation.theme.Orange
import com.example.studyassistant.presentation.theme.Red
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

enum class Priority(val title: String, val color: Color, val value: Int){
    LOW(title = "Low", color = Green, value = 0),
    MEDIUM(title = "Medium", color = Orange, value = 1),
    HIGH(title = "High", color = Red, value = 2);

    companion object{
        fun fromInt(value: Int) = Priority.entries.firstOrNull{ it.value == value} ?: MEDIUM
    }
}

object CurrentOrFutureSelectableDates: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val currentDateMillis =
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return utcTimeMillis >= currentDateMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDate.now().year
    }
}

fun Long?.changeMillisToDateString(): String{
    val date : LocalDate = this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MM yyyy"))
}