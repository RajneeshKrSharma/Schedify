package com.unique.schedify.post_auth.schedule_list.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.schedule_list.remote.dto.ScheduleListResponseDto
import com.unique.schedify.post_auth.schedule_list.use_case.GetScheduleListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleListViewModel @Inject constructor(
    private val scheduleListUseCase: GetScheduleListUseCase,
): ViewModel() {

    private var _addAddressUpdate = mutableStateOf<Resource<Boolean>>(Resource.Default())
    val addAddressUpdate: State<Resource<Boolean>> = _addAddressUpdate

    private var _fileUploaded = mutableStateOf<Resource<Boolean>>(Resource.Default())
    val fileUploaded: State<Resource<Boolean>> = _fileUploaded

    private var _scheduleItems = mutableStateOf<Resource<List<ScheduleListResponseDto.Data?>>>(Resource.Default())
    val scheduleItems: State<Resource<List<ScheduleListResponseDto.Data?>>> = _scheduleItems

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
                        _scheduleItems.value = Resource.Error(this.errorMessage)

                    }
                }
            }
        }

    }

}