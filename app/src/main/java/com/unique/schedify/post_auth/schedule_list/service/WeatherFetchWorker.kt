package com.unique.schedify.post_auth.schedule_list.service

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.schedule_list.data.local.ScheduleItemEntity
import com.unique.schedify.post_auth.schedule_list.data.remote.model.GetWeatherStatusRequestDto
import com.unique.schedify.post_auth.schedule_list.domain.repository.ScheduleRepository
import com.unique.schedify.post_auth.schedule_list.use_case.GetWeatherStatusUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


@HiltWorker
class WeatherFetchWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ScheduleRepository,
    private val getWeatherStatusUseCase: GetWeatherStatusUseCase,
) : CoroutineWorker(context.applicationContext, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        Log.i("TaG", "doWork: Initiate")

        // 1. Do your task
        Log.d("Worker", "Running at: ${System.currentTimeMillis()}")


        val currentTime = getCurrentDateTimeInISOFormat()
        val getNotifiableItems = repository.getItemsToNotify(currentTime, convertFromIso(currentTime))

        Log.i("TaG", "doWork: Notifiable items: $getNotifiableItems")
        Log.i("TaG", "doWork: data size: ${getNotifiableItems.size}")

        getNotifiableItems.forEach { item ->
            val reqData = GetWeatherStatusRequestDto(
                pincode = item.pincode,
                scheduleItemId = item.id.toString()
            )

            val response = getWeatherStatusUseCase.execute(reqData)

            Log.i(TAG, "checkAndNotifyItems: Response for item ${item.id}: $response")
            with(response) {
                if (this is ApiResponseResource.Success) {
                    val weatherDetails = this.data.weatherNotifyDetails
                    if (weatherDetails != null) {
                        weatherDetails[0]?.let{
                            val nextNotifyAt = it.nextNotifyAt
                            updateItemNextNotifyAt( item, nextNotifyAt)
                        } ?: run {
                            Log.w(TAG, "run block No weather details found for item: ${item.id}")
                        }
                    } else {
                        Log.w(TAG, "No weather details found for item: ${item.id}")
                    }
                } else {
                    Log.e(TAG, "Error fetching weather data: ")
                }
            }

        }

        // 2. Schedule next execution
        val nextRequest = OneTimeWorkRequestBuilder<WeatherFetchWorker>()
            .setInitialDelay(3, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "RecurringEvery3Min",             // always same name
            ExistingWorkPolicy.APPEND,   // replace any old one
            nextRequest
        )


        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertFromIso(isoStr: String): String {
        val zonedDateTime = ZonedDateTime.parse(isoStr)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return outputFormatter.format(zonedDateTime)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun updateItemNextNotifyAt( item: ScheduleItemEntity, nextNotifyAtString: String?) {

        val updatedData = ScheduleItemEntity(
            id = item.id,
            dateTime = item.dateTime,
            title = item.title,
            pincode = item.pincode,
            nextNotifyAt = nextNotifyAtString
        )

        repository.updateItem(updatedData)
    }


    fun getCurrentDateTimeInISOFormat(): String {
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata") // Set to IST
        return dateFormat.format(date)
    }

    companion object {
        private const val TAG = "TaG"
    }
}