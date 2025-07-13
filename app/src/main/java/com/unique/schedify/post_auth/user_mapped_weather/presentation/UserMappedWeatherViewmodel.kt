package com.unique.schedify.post_auth.user_mapped_weather.presentation

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.unique.schedify.core.domain.workers.AutoRescheduleWorker
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.AppTourEntity
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.PreAuthDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserMappedWeatherViewmodel @Inject constructor(
    private val preAuthDao: PreAuthDao
) : ViewModel() {

    private val _workerData = mutableStateOf<List<AppTourEntity>>(emptyList())
    val workerData: State<List<AppTourEntity>> = _workerData

    fun startTestWorker(context: Context) {
        val firstWork = OneTimeWorkRequestBuilder<AutoRescheduleWorker>()
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            AutoRescheduleWorker.AUTO_RESCHEDULE_WORKER,
            ExistingWorkPolicy.REPLACE,
            firstWork
        )
    }

    init {
        getWorkerData()
    }

    private fun getWorkerData() {
        viewModelScope.launch {
           _workerData.value = preAuthDao.getAppTourData()
        }
    }
}