package com.unique.schedify.core.presentation.download_and_save_ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
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

    private val _stepNumber = mutableIntStateOf(1)
    val stepNumber: State<Int> = _stepNumber

    private val _notificationPermissionGranted = mutableStateOf(false)
    val notificationPermissionGranted: State<Boolean> = _notificationPermissionGranted

    private val _permissionRequested = mutableStateOf(false)
    val permissionRequested: State<Boolean> = _permissionRequested

    private val _shouldShowSettings = mutableStateOf(false)
    val shouldShowSettings: State<Boolean> = _shouldShowSettings

    private val _permissionInSettingsFlow = mutableStateOf(false)
    val permissionInSettingsFlow: State<Boolean> = _permissionInSettingsFlow


    private val _isNotificationPermissionEligible = mutableStateOf(false)
    val isNotificationPermissionEligible: State<Boolean> = _isNotificationPermissionEligible

    companion object {
        const val WORKER_SCHEDIFY = "WORKER_SCHEDIFY"
    }

    /**
     * Called after permission dialog or return from settings
     */
    fun onPermissionResult(isGranted: Boolean, shouldShowRationale: Boolean) {
        _notificationPermissionGranted.value = isGranted

        if (isGranted) {
            // ✅ If granted: clear all flags
            _permissionRequested.value = false
            _shouldShowSettings.value = false
            _permissionInSettingsFlow.value = false
            _stepNumber.intValue = 2
        } else {
            // ❌ Denied
            _permissionRequested.value = true
            _shouldShowSettings.value = !shouldShowRationale
            _stepNumber.intValue = 1
            // Don't reset permissionInSettingsFlow here, wait for Lifecycle.ON_RESUME to handle that
        }
    }

    fun checkIsNotificationPermissionEligible(isEligible: Boolean) {
        _isNotificationPermissionEligible.value = isEligible
    }

    /**
     * Called before launching permission dialog again
     */
    fun resetRequestState() {
        _permissionRequested.value = false
        _stepNumber.intValue = 1
    }

    /**
     * Called before opening app notification settings
     */
    fun markSettingsFlowStarted() {
        _permissionInSettingsFlow.value = true
        _stepNumber.intValue = 1
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