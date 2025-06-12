package com.unique.schedify.core.util.notification_utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.text.HtmlCompat
import com.google.firebase.messaging.FirebaseMessagingService.NOTIFICATION_SERVICE
import com.unique.schedify.MainActivity
import com.unique.schedify.R
import com.unique.schedify.notifications.utility.NOTIFICATION_CHANNEL_ALERT
import com.unique.schedify.notifications.utility.NOTIFICATION_CHANNEL_DEFAULT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Random

object NotificationUtils {

    @SuppressLint("UnspecifiedImmutableFlag")
    suspend fun showNotification(
        context: Context,
        title: String,
        body: String,
        channel: String,
        receivedImageUrl: String? = null,
        uniqueId: String,
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val (channelId, channelName) = when (channel) {
                NOTIFICATION_CHANNEL_ALERT -> NOTIFICATION_CHANNEL_ALERT to "These notifications are for alert purpose."
                else -> NOTIFICATION_CHANNEL_DEFAULT to "These notifications are for promotion purpose."
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(channelId, channelName, importance)
                mChannel.enableLights(true)
                mChannel.enableVibration(true)
                notificationManager.createNotificationChannel(mChannel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("channel", channel)
                putExtra("uniqueId", uniqueId)
                action = System.currentTimeMillis().toString()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val notifyPendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )

            val bitmap = receivedImageUrl?.takeIf { it.isEmpty() }?.let { url -> downloadBitmapFromUrl(url) }
                ?: BitmapFactory.decodeResource(context.resources, R.drawable.weather)

            withContext(Dispatchers.Main) {
                val mBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.schedify)
                    .setContentTitle(HtmlCompat.fromHtml("<b>$title</b>", HtmlCompat.FROM_HTML_MODE_LEGACY))
                    .setContentText(body)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setContentIntent(notifyPendingIntent)
                    .setLargeIcon(bitmap)
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null as Bitmap?))
                    .setAutoCancel(true)

                val randomId = Random().nextInt(9999 - 1000) + 1000
                notificationManager.notify(randomId, mBuilder.build())
            }

            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
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