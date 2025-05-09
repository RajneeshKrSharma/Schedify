package com.unique.schedify.post_auth.schedule_list.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AddScheduleItemDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddScheduleItemRequestModel
import com.unique.schedify.post_auth.schedule_list.use_case.AddScheduleListUseCase
import com.unique.schedify.post_auth.schedule_list.use_case.GetScheduleListUseCase
import com.unique.schedify.post_auth.schedule_list.use_case.VerifyPinCodeCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimpleScheduleListViewModel @Inject constructor(
    private val scheduleListUseCase: GetScheduleListUseCase,
    private val addScheduleItemUseCase: AddScheduleListUseCase,
    private val verifyPinCodeCase: VerifyPinCodeCase,
): ViewModel() {


    private var _pinCodeValidationSuccess = mutableStateOf<Resource<Boolean>>(Resource.Default())
    val pinCodeValidationSuccess: State<Resource<Boolean>> = _pinCodeValidationSuccess

    // LiveData or StateFlow to hold schedule items
    private var _scheduleItems = mutableStateOf<Resource<List<ScheduleListResponseDto.Data?>>>(Resource.Default())
    val scheduleItems: State<Resource<List<ScheduleListResponseDto.Data?>>> = _scheduleItems

    private var _addedScheduleItem = mutableStateOf<Resource<AddScheduleItemDto.Data?>>(Resource.Default())
    val addedScheduleItem: State<Resource<AddScheduleItemDto.Data?>> = _addedScheduleItem

    // Function to update schedule list
    fun setScheduleItems(items: List<ScheduleListResponseDto.Data?>) {
        _scheduleItems.value = Resource.Success(items)
    }

    init {

        viewModelScope.launch {
            _scheduleItems.value = Resource.Loading()
            with(scheduleListUseCase.execute()) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        _scheduleItems.value =  Resource.Success(this.data.data)

                    }
                    is ApiResponseResource.Error -> {
                        // Handle error
                        Log.i("TaG", "error: ${this.errorMessage}")
                    }
                }
            }
        }

    }




    fun addScheduleItem(item: AddScheduleItemRequestModel) {
        _addedScheduleItem.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            with(addScheduleItemUseCase.execute(item)) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        Log.i("TaG", "success: ${this.data}")

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
                            userId = this.data.data?.userId
                        )

                        val updatedScheduleItemsList = _scheduleItems.value.data?.toMutableList() ?: mutableListOf()
                        updatedScheduleItemsList.add(scheduleItem)
                        setScheduleItems(updatedScheduleItemsList.toList())

                        _addedScheduleItem.value = Resource.Success(this.data.data)
                    }
                    is ApiResponseResource.Error -> {
                        Log.i("TaG", "error: ${this.errorMessage}")
                        _addedScheduleItem.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

    fun verifyPinCode(pinCode: String) {
        _pinCodeValidationSuccess.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            with(verifyPinCodeCase.execute(pinCode)) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        Log.i("TaG", "success: ${this.data}")
                        _pinCodeValidationSuccess.value = Resource.Success(true)
                    }
                    is ApiResponseResource.Error -> {

                        Log.i("TaG", "error: ${this.errorMessage}")
                        _pinCodeValidationSuccess.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

}