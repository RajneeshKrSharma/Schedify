package com.unique.schedify.post_auth.post_auth_loading.data.remote.dto

data class PushNotifyRequest(
    val title: String,
    val body: String,
    val channel: String,
    val token: String,
    val weather_image_url: String,
    val uniqueId: String,
) {
    companion object {
        fun default(): PushNotifyRequest = PushNotifyRequest(
            title = "Testing Push Notify",
            body = "",
            channel = "weather_updates",
            token = "",
            weather_image_url = "https://schedify.pythonanywhere.com/media/pictures/rain.jpg",
            uniqueId = "${System.currentTimeMillis()}"
        )
    }
}
