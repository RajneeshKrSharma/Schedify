package com.unique.schedify.core.util.date_utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtility {

    private const val APP_TIME_ZONE = "Asia/Kolkata"

    @SuppressLint("WeekBasedYear")
    fun convertTimeTokenToReadable(timeToken: Long): String {
        val date = Date(timeToken) // Convert milliseconds to Date
        val format = SimpleDateFormat(
            DateFormat.FORMAT_SPACED_SHORT_DD_MMM_YYYY_COLON_HH_MM_SS_AA.format, Locale.getDefault()
        )
        return format.format(date) // Format date into a readable string
    }


    fun getCurrentDateTimeString(
        pattern: String = DateFormat.FORMAT_MMM_DD_YYYY_HH_MM_AA.format,
        timeZoneId: String = APP_TIME_ZONE
    ): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone(timeZoneId)
        return formatter.format(Date(System.currentTimeMillis()))
    }
}

fun formattedServerDateTime(dateTime: String?): String {
    return dateTime?.split(".")?.firstOrNull()?.takeIf { it.isNotBlank() }?.let {
        runCatching {
            val inputFormat = SimpleDateFormat(DateFormat.FORMAT_SERVER_TIME.format, Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }
            val outputFormat = SimpleDateFormat(DateFormat.FORMAT_MMM_DD_YYYY_HH_MM_AA.format, Locale.getDefault())

            inputFormat.parse(it)?.let { date ->
                outputFormat.format(date)
            }
        }.getOrElse { "" }
    } ?: ""
}
