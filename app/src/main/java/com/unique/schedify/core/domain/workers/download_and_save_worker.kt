package com.unique.schedify.core.domain.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.unique.schedify.core.di.ApiHandlerEntryPoint
import com.unique.schedify.core.presentation.download_and_save_ui.utility.FAILURE_MSG
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoints

@HiltWorker
class DownloadAndSaveWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context.applicationContext, workerParams) {

    companion object {
        const val KEY = "KEY"
        const val PRE_AUTH_TAG = "PRE_AUTH_TAG"
        const val POST_AUTH_TAG = "POST_AUTH_TAG"
    }

    override suspend fun doWork(): Result {
        return try {
            val apiToCall = inputData.getString(KEY) ?: return Result.failure(workDataOf(FAILURE_MSG to "apiToCall is null"))

            if (apiToCall.isBlank()) {
                return Result.failure()
            }

            val handlerMap = EntryPoints
                .get(applicationContext, ApiHandlerEntryPoint::class.java)
                .handlerMap

            val tag = tags.firstOrNull { it != this::class.java.name }

            val result = tag?.let { handlerMap[it] }?.callApi(apiToCall)
            result ?: Result.failure(workDataOf(FAILURE_MSG to "result data is null"))
        } catch (e: Exception) {
            // Optional: Log error
            e.printStackTrace()

            // Return failure with error details if needed
            Result.failure(workDataOf(FAILURE_MSG to e.message))
        }
    }
}