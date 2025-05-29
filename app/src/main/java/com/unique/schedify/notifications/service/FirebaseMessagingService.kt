package com.unique.schedify.notifications.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import com.unique.schedify.MainActivity
import com.unique.schedify.R
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.notifications.utility.NOTIFICATION_CHANNEL_ALERT
import com.unique.schedify.notifications.utility.NOTIFICATION_CHANNEL_DEFAULT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Random
import javax.inject.Inject

@AndroidEntryPoint
class SchedifyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPrefConfig: SharedPrefConfig

    private lateinit var pendingIntent: Intent

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

        showNotification(
            this,
            data["title"] ?: "NA",
            data["body"] ?: "NA",
            data["channel"] ?: "NA",
            data["weather_image_url"] ?: "NA",
            data["uniqueId"] ?: "NA")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(
        context: Context,
        title: String,
        body: String,
        channel: String,
        receivedImageUrl: String,
        uniqueId: String,
    ) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId: String
        val channelName: String

        when (channel) {
            NOTIFICATION_CHANNEL_ALERT -> {
                channelId   = NOTIFICATION_CHANNEL_ALERT
                channelName = "These notifications are for alert purpose."
            }
            else -> {
                channelId   = NOTIFICATION_CHANNEL_DEFAULT
                channelName = "These notifications are for promotion purpose."
            }
        }

        /*val sound =
            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.notification).toUri()
        val attributes: AudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            mChannel.enableLights(true)
            mChannel.enableVibration(true)
            //mChannel.setSound(sound, attributes)
            notificationManager.createNotificationChannel(mChannel)
        }

        pendingIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("channel", NOTIFICATION_CHANNEL_ALERT)
            putExtra("uniqueId", uniqueId)
            action = System.currentTimeMillis().toString()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val notifyPendingIntent = PendingIntent.getActivity(this, 0, pendingIntent, PendingIntent.FLAG_IMMUTABLE)

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = downloadBitmapFromUrl(receivedImageUrl)
                ?: BitmapFactory.decodeResource(context.resources, R.drawable.weather) // fallback image

            withContext(Dispatchers.Main) {
                val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.schedify)
                    .setContentTitle(HtmlCompat.fromHtml("<b>$title</b> \uD83C\uDF27\uFE0F" , HtmlCompat.FROM_HTML_MODE_LEGACY))
                    .setContentText(body)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setContentIntent(notifyPendingIntent)
                    .setLargeIcon(bitmap)
                    .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null as Bitmap?)
                    )
                    .setAutoCancel(true)

                val randomId = Random().nextInt(9999 - 1000) + 1000

                notificationManager.notify(randomId, mBuilder.build())
            }
        }
    }

    // Suspend function to download bitmap safely in IO context
    private suspend fun downloadBitmapFromUrl(urlString: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}