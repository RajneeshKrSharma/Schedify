package com.unique.schedify.core.presentation.download_and_save_ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.domain.workers.DownloadAndSaveWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DownloadAndSaveViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val sharedPrefConfig: SharedPrefConfig
): ViewModel() {

    private val _state = MutableStateFlow<Set<String>>(emptySet())
    val state: StateFlow<Set<String>> = _state

    private val _notificationPermissionGranted = mutableStateOf(false)
    val notificationPermissionGranted: State<Boolean> = _notificationPermissionGranted

    private val _shouldShowSettings = mutableStateOf(false)
    val shouldShowSettings: State<Boolean> = _shouldShowSettings


    private val _isNotificationPermissionEligible = mutableStateOf(false)
    val isNotificationPermissionEligible: State<Boolean> = _isNotificationPermissionEligible

    companion object {
        const val WORKER_SCHEDIFY = "WORKER_SCHEDIFY"
    }

    /**
     * Called after permission dialog or return from settings
     */
    fun onPermissionResult(isGranted: Boolean) {
        _notificationPermissionGranted.value = isGranted
    }

    fun shouldShowNotificationSetting(isShow: Boolean) { // when value is true then do not show setting
        _shouldShowSettings.value = isShow
    }

    fun checkIsNotificationPermissionEligible(isEligible: Boolean) {
        _isNotificationPermissionEligible.value = isEligible
    }

    private fun updateState(newValue: String) {
        val updatedSet = _state.value + newValue
        _state.value = updatedSet
    }

    fun startWorker(
        apisToCall:List<String>,
        tag: String
    ) {
        val constraints = Constraints.Builder().build()
        val workManager = WorkManager.getInstance(context)

        workManager.cancelAllWorkByTag(tag)
        workManager.pruneWork()

        // Create the first request
        var continuation = workManager.beginUniqueWork(
            tag,
            ExistingWorkPolicy.REPLACE,
            createRequest(apisToCall[0], constraints, tag)
        )

        // Chain remaining requests
        apisToCall.subList(1, apisToCall.size).forEach { api ->
            continuation = continuation.then(createRequest(api, constraints, tag))
        }

        continuation.enqueue()

        workManager.getWorkInfosByTagLiveData(tag).observeForever { workInfos ->
            workInfos.forEach { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> {
                        Log.d(WORKER_SCHEDIFY, "ENQUEUED")
                    }
                    WorkInfo.State.RUNNING -> {
                        Log.d(WORKER_SCHEDIFY, "RUNNING")

                    }
                    WorkInfo.State.SUCCEEDED -> {
                        workInfo.outputData.getString("api")?.let { data ->
                            updateState(data)
                        }
                        Log.d(WORKER_SCHEDIFY, "SUCCEEDED: ${workInfo.outputData}")
                    }
                    WorkInfo.State.FAILED -> {
                        Log.d(WORKER_SCHEDIFY, "FAILED")

                    }
                    WorkInfo.State.BLOCKED -> {
                        Log.d(WORKER_SCHEDIFY, "BLOCKED")

                    }
                    WorkInfo.State.CANCELLED -> {
                        Log.d(WORKER_SCHEDIFY, "CANCELLED")
                    }
                }
            }
        }
    }

    private fun createRequest(
        apiName: String,
        constraints: Constraints,
        tag: String
    ): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<DownloadAndSaveWorker>()
            .setConstraints(constraints)
            .setInputData(workDataOf(DownloadAndSaveWorker.KEY to apiName))
            .addTag(tag)
            .build()
    }
}