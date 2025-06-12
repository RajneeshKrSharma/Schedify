package com.unique.schedify.core.domain.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.unique.schedify.core.util.date_utils.DateUtility
import com.unique.schedify.core.util.notification_utils.NotificationUtils
import com.unique.schedify.notifications.utility.NOTIFICATION_CHANNEL_ALERT
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.AppTourEntity
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.PreAuthDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.Random
import java.util.concurrent.TimeUnit

@HiltWorker
class AutoRescheduleWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val preAuthDao: PreAuthDao
) : Worker(context.applicationContext, workerParams) {

    companion object {
        const val AUTO_RESCHEDULE_WORKER = "RecurringEvery3Min"
        const val AUTO_RESCHEDULE_AFTER_MINS = 3L
    }

    override fun doWork(): Result {
        // 1. Do your task
        Log.d("Worker", "Running at: ${System.currentTimeMillis()}")

        val currentDateTime = DateUtility.getCurrentDateTimeString()

        runBlocking {
            val notificationTriggerStatus = triggerNotifications(
                context = context,
                notificationTime = currentDateTime
            )
            preAuthDao.insertAppTourData(
                AppTourEntity(
                    image = "Work: $id",
                    subtitle = currentDateTime,
                    title = "Notifying Successful : $notificationTriggerStatus"
                )
            )
        }

        // 2. Schedule next execution
        val nextRequest = OneTimeWorkRequestBuilder<AutoRescheduleWorker>()
            .setInitialDelay(AUTO_RESCHEDULE_AFTER_MINS, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            AUTO_RESCHEDULE_WORKER,
            ExistingWorkPolicy.APPEND,
            nextRequest
        )

        return Result.success()
    }

    private suspend fun triggerNotifications(
        context: Context,
        notificationTime: String
    ): Boolean {
        val notificationTask = CoroutineScope(Dispatchers.Default).async {
            NotificationUtils.showNotification(
                context = context,
                title = "AutoRescheduleWorker",
                body = "Notified at $notificationTime",
                channel = NOTIFICATION_CHANNEL_ALERT,
                uniqueId = Random().nextInt().toString()
            )
        }

        return notificationTask.await()
    }
}