package com.unique.schedify.post_auth.schedule_list.presentation

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.schedule_list.data.local.ScheduleItemEntity
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AddScheduleItemDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddAddressRequestDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddScheduleItemRequestModel
import com.unique.schedify.post_auth.schedule_list.data.remote.model.GetWeatherStatusRequestDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.UploadFileRequestDto
import com.unique.schedify.post_auth.schedule_list.domain.repository.ScheduleRepository
import com.unique.schedify.post_auth.schedule_list.service.WeatherFetchWorker
import com.unique.schedify.post_auth.schedule_list.use_case.AddAddressCase
import com.unique.schedify.post_auth.schedule_list.use_case.AddScheduleListUseCase
import com.unique.schedify.post_auth.schedule_list.use_case.GetScheduleListUseCase
import com.unique.schedify.post_auth.schedule_list.use_case.GetWeatherStatusUseCase
import com.unique.schedify.post_auth.schedule_list.use_case.UploadFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SimpleScheduleListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scheduleListUseCase: GetScheduleListUseCase,
    private val addScheduleItemUseCase: AddScheduleListUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val addAddressCase: AddAddressCase,
    private val sharedPrefConfig: SharedPrefConfig,
    private val getWeatherStatusUseCase: GetWeatherStatusUseCase,
    private val dbRepo : ScheduleRepository
): ViewModel() {

    private var _addAddressUpdate = mutableStateOf<Resource<Boolean>>(Resource.Default())
    val addAddressUpdate: State<Resource<Boolean>> = _addAddressUpdate

    private var _fileUploaded = mutableStateOf<Resource<Boolean>>(Resource.Default())
    val fileUploaded: State<Resource<Boolean>> = _fileUploaded

    // LiveData or StateFlow to hold schedule items
    private var _scheduleItems = mutableStateOf<Resource<List<ScheduleListResponseDto.Data?>>>(Resource.Default())
    val scheduleItems: State<Resource<List<ScheduleListResponseDto.Data?>>> = _scheduleItems

    private var _addedScheduleItem = mutableStateOf<Resource<AddScheduleItemDto.Data?>>(Resource.Default())
    val addedScheduleItem: State<Resource<AddScheduleItemDto.Data?>> = _addedScheduleItem

    private var _isAddScheduleComplete = mutableStateOf<Resource<Boolean>>(Resource.Default())

    // Function to update schedule list
    fun setScheduleItems(items: List<ScheduleListResponseDto.Data?>) {
        _scheduleItems.value = Resource.Success(items)
    }

    init {

        startTextWorker()

        viewModelScope.launch {
            _scheduleItems.value = Resource.Loading()
            with(scheduleListUseCase.execute()) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        _scheduleItems.value =  Resource.Success(this.data.data)

                    }
                    is ApiResponseResource.Error -> {
                        // Handle error
                        _scheduleItems.value = Resource.Error(this.errorMessage)

                    }
                }
            }
        }

    }


    fun addScheduleItem(item: AddScheduleItemRequestModel, attachedFiles: List<File>) {
        _addedScheduleItem.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            with(addScheduleItemUseCase.execute(item)) {
                when (this) {
                    is ApiResponseResource.Success -> {

                        val scheduleItem = ScheduleListResponseDto.Data(
                            attachments = this.data.data?.attachments,
                            dateTime = this.data.data?.dateTime,
                            googleAuthUserId = this.data.data?.googleAuthUserId,
                            id = this.data.data?.id,
                            isArchived = this.data.data?.isArchived,
                            isItemPinned = this.data.data?.isItemPinned,
                            lastScheduleOn = this.data.data?.lastScheduleOn,
                            priority = this.data.data?.priority,
                            subTitle = this.data.data?.subTitle,
                            title = this.data.data?.title,
                            userId = this.data.data?.userId,
                            isWeatherNotifyEnabled = item.isWeatherNotifyEnabled
                        )

                        if (attachedFiles.isNotEmpty()) {
                            uploadFiles(this.data.data?.id.toString(), attachedFiles, scheduleItem)
                        } else {
                            val updatedScheduleItemsList = scheduleItems.value.data?.toMutableList() ?: mutableListOf()
                            updatedScheduleItemsList.add(scheduleItem)
                            setScheduleItems(updatedScheduleItemsList.toList())
                        }
                        updateAddressInfo()
                        getWeatherStatus(scheduleItemId = this.data.data?.id.toString())

                        viewModelScope.launch {
                            // Wait until _isAddScheduleComplete is not Default or Loading
                            val result = flow {
                                while (_isAddScheduleComplete.value is Resource.Default || _isAddScheduleComplete.value is Resource.Loading) {
                                    delay(100) // polling delay
                                }
                                emit(_isAddScheduleComplete.value)
                            }.first()

                            when (result) {
                                is Resource.Success -> {

                                    _addedScheduleItem.value = Resource.Success(this@with.data.data)
                                }
                                is Resource.Error -> {
                                    _addedScheduleItem.value = Resource.Error(result.message ?: "Failed to process weather get Status Api detail")
                                }
                                else -> {
                                    _addedScheduleItem.value = Resource.Error("Unexpected error occurred.")
                                }
                            }
                        }
                    }
                    is ApiResponseResource.Error -> {
                        _addedScheduleItem.value = Resource.Error(this.errorMessage)
                        delay(1000)
                        _addedScheduleItem.value = Resource.Default() // Reset state after error
                    }
                }
            }
        }
    }

    fun uploadFiles(
        scheduleId: String,
        attachedFiles: List<File>,
        scheduleItem: ScheduleListResponseDto.Data
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val fileParts = attachedFiles.mapNotNull { file ->
                try {
                    prepareFilePart("files", file)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null // skip files that can't be converted
                }
            }

            val req = UploadFileRequestDto(
                scheduleId = scheduleId,
                files = fileParts
            )
            val updatedScheduleItemsList = scheduleItems.value.data?.toMutableList() ?: mutableListOf()

            with(uploadFileUseCase.execute(req)) {
                when (this) {
                    is ApiResponseResource.Success -> {

                        scheduleItem.attachments = this.data.attachments

                        updatedScheduleItemsList.add(scheduleItem)
                        setScheduleItems(updatedScheduleItemsList.toList())

                        _fileUploaded.value = Resource.Success(true)
                    }
                    is ApiResponseResource.Error -> {
                        updatedScheduleItemsList.add(scheduleItem)
                        setScheduleItems(updatedScheduleItemsList.toList())
                        _fileUploaded.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

    fun updateAddressInfo() {
        viewModelScope.launch ( Dispatchers.IO ) {
            val addressInfoReq = AddAddressRequestDto(
                                    pinCode = sharedPrefConfig.getUserPincode().toString(),
                                    address = sharedPrefConfig.getUserCompleteAddress().toString()
                                )
            with( addAddressCase.execute(addressInfoReq)) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        _addAddressUpdate.value = Resource.Success(true)
                    }
                    is ApiResponseResource.Error -> {
                        _addAddressUpdate.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

    fun getAddressInfo(): String {
        return sharedPrefConfig.getUserCompleteAddress().toString()
    }

    fun getWeatherStatus(scheduleItemId : String) {
        viewModelScope.launch(Dispatchers.IO) {
            val pincode = sharedPrefConfig.getUserPincode().toString()
            with(getWeatherStatusUseCase.execute(GetWeatherStatusRequestDto(pincode, scheduleItemId))) {
                when (this) {
                    is ApiResponseResource.Success -> {

                        (this.data.weatherNotifyDetails.firstOrNull{
                            it?.scheduleItem?.id == scheduleItemId.toInt() && it.pincode == pincode
                        }?.let { weatherNotifyData ->
                            with(weatherNotifyData) {
                                nextNotifyAt?.let { nextNotifyAtNotNull ->
                                    scheduleItem?.dateTime?.let {  scheduleDateTimeNotNull ->
                                       (nextNotifyAtNotNull to scheduleDateTimeNotNull)
                                    }
                                }?.let { (nextNotifyAtNotNull, scheduleDateTimeNotNull) ->
                                    dbRepo.insertOrUpdate(ScheduleItemEntity(
                                        id = (weatherNotifyData.scheduleItem?.id ?: scheduleItemId.toInt()),
                                        title = weatherNotifyData.scheduleItem?.title ?: "",
                                        dateTime = scheduleDateTimeNotNull,
                                        pincode =  weatherNotifyData.pincode ?: pincode,
                                        nextNotifyAt = nextNotifyAtNotNull
                                    ))
                                    _isAddScheduleComplete.value = Resource.Success(true)

                                }
                            }
                        }) ?: run {
                           _isAddScheduleComplete.value = Resource.Error("Getting Issue for fetching weather notify details for schedule item ID: $scheduleItemId")
                        }

}
                    is ApiResponseResource.Error -> {
                        _isAddScheduleComplete.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

    private fun prepareFilePart(paramName: String, file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, file.name, requestFile)
    }

    fun startTextWorker() {
        val firstWork = OneTimeWorkRequestBuilder<WeatherFetchWorker>()
            .setConstraints(
                    Constraints.Builder()
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .setRequiresBatteryNotLow(false)
                        .setRequiresStorageNotLow(false)
                        .setRequiresDeviceIdle(false)
                        .build()
                ) // Ensure network connectivity
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "RecurringEvery3Min",
            ExistingWorkPolicy.REPLACE, // replace if already scheduled
            firstWork
        )
    }

    fun resetAddScheduleListState() {
//        _scheduleItems.value = Resource.Default()
        _addedScheduleItem.value = Resource.Default()
        _fileUploaded.value = Resource.Default()
        _addAddressUpdate.value = Resource.Default()
        _isAddScheduleComplete.value = Resource.Default()
    }



}