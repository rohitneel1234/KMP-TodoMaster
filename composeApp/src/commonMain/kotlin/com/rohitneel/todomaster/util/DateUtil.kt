package com.rohitneel.todomaster.util

import kotlinx.datetime.*

object DateUtil {

    fun getTimeDiff(timeInMillis: Long): Long {
        val systemTimeInMillis = Clock.System.now().toEpochMilliseconds()
        return timeInMillis - systemTimeInMillis
    }

    // Basic implementation for commonMain, might need platform specific actuals for full formatting
    fun getDate(date: Long): String {
        val instant = Instant.fromEpochMilliseconds(date)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.dayOfMonth}-${dateTime.monthNumber}-${dateTime.year % 100} ${dateTime.hour}:${dateTime.minute}"
    }

    fun getDateForOverview(date: Long): String {
        val instant = Instant.fromEpochMilliseconds(date)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.dayOfMonth} ${dateTime.monthNumber}, ${dateTime.year % 100}"
    }

    fun getTime(dueDate: Long): String {
        val instant = Instant.fromEpochMilliseconds(dueDate)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.hour}:${dateTime.minute}"
    }

    fun getCurrentTimestampMillis(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}
