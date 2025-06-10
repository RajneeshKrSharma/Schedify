package com.unique.schedify.core.presentation.download_and_save_ui.utility


enum class PreAuthDownloadApis {
    PRE_AUTH_DETAIL;
}

enum class PostAuthDownloadApis {
    POST_AUTH_DETAIL,
    USER_MAPPED_WEATHER_STATUS_DETAILS,
}

object ApisList {
    val preAuthApisList = listOf(
        PreAuthDownloadApis.PRE_AUTH_DETAIL,
    )

    val postAuthApisList = listOf(
        PostAuthDownloadApis.POST_AUTH_DETAIL,
        PostAuthDownloadApis.USER_MAPPED_WEATHER_STATUS_DETAILS,
    )
}