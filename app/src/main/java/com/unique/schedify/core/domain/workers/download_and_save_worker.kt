package com.unique.schedify.core.domain.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unique.schedify.core.di.ApiHandlerEntryPoint
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
        val apiToCall = inputData.getString(KEY) ?: return Result.failure()

        if (apiToCall.isBlank()) {
            return Result.failure()
        }

        val handlerMap =
            EntryPoints.get(applicationContext, ApiHandlerEntryPoint::class.java).handlerMap

        val tag = tags.firstOrNull()

        return tag?.let { tagNotNull -> handlerMap[tagNotNull] }?.callApi(apiToCall)
            ?: Result.failure()
    }
}