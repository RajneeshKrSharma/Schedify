package com.unique.schedify.notifications.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.util.notification_utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SchedifyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPrefConfig: SharedPrefConfig

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        Log.d("FCM", "New Token: $newToken")
        sharedPrefConfig.saveFcmToken(newToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data

        Log.d("FCM", "remoteMessage: $remoteMessage")
        Log.d("FCM", "remoteMessage.data: " + remoteMessage.data)
        Log.d("FCM", "Title: " + data["title"])
        Log.d("FCM", "Notification Message Body: " + data["body"])
        Log.d("FCM", "Notification Message Channel: " + data["channel"])
        Log.d("FCM", "Notification Message weatherType: " + data["weatherType"])
        Log.d("FCM", "Notification Message uniqueId: " + data["uniqueId"])

        CoroutineScope(Dispatchers.Default).launch {
            val result = NotificationUtils.showNotification(
                context = this@SchedifyFirebaseMessagingService,
                title = data["title"] ?: "NA",
                body = data["body"] ?: "NA",
                channel = data["channel"] ?: "NA",
                receivedImageUrl = data["weather_image_url"] ?: "NA",
                uniqueId = data["uniqueId"] ?: "NA"
            )

            Log.d("FCM", "Notification shown: $result")
        }
    }
}