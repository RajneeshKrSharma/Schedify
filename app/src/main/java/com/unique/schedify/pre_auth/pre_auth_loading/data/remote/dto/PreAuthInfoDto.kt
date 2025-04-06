package com.unique.schedify.pre_auth.pre_auth_loading.data.remote.dto


import com.google.gson.annotations.SerializedName

data class PreAuthInfoDto(
    @SerializedName("app_specific_details")
    val appSpecificDetails: AppSpecificDetails?,
    @SerializedName("app_tour_info")
    val appTourInfo: List<AppTourInfo?>?,
    @SerializedName("app_update_info")
    val appUpdateInfo: AppUpdateInfo?
) {
    data class AppSpecificDetails(
        @SerializedName("language_code")
        val languageCode: Int?,
        @SerializedName("theme")
        val theme: Int?
    ) {
        companion object {
            fun empty(): AppSpecificDetails = AppSpecificDetails(
                languageCode = 0,
                theme = 0
            )
        }
    }

    data class AppTourInfo(
        @SerializedName("image")
        val image: String?,
        @SerializedName("subtitle")
        val subtitle: String?,
        @SerializedName("title")
        val title: String?
    ) {
        companion object {
            fun empty(): AppTourInfo = AppTourInfo(
                image = "",
                subtitle = "",
                title = ""
            )
        }
    }

    data class AppUpdateInfo(
        @SerializedName("current_version")
        val currentVersion: String?,
        @SerializedName("redirect_url")
        val redirectUrl: String?,
        @SerializedName("update_mode")
        val updateMode: Int?
    ) {
        companion object {
            fun empty(): AppUpdateInfo = AppUpdateInfo(
                currentVersion = "",
                redirectUrl = "",
                updateMode = 0
            )
        }
    }

    companion object {
        fun empty(): PreAuthInfoDto = PreAuthInfoDto(
            appSpecificDetails = AppSpecificDetails.empty(),
            appTourInfo = emptyList(),
            appUpdateInfo = AppUpdateInfo.empty()
        )
    }
}