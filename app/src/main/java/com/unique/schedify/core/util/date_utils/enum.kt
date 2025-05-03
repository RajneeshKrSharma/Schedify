package com.unique.schedify.core.util.date_utils

enum class DateFormat(val format: String) {
    FORMAT_SPACED_SHORT_DD_MMM_YYYY_COLON_HH_MM_SS_AA("dd MMM, YYYY HH:mm:ss aa"),
    FORMAT_SERVER_TIME("yyyy-MM-dd'T'HH:mm:ss"),
    FORMAT_MMM_DD_YYYY_HH_MM_AA("MMM dd yyyy, h:mm a")
}