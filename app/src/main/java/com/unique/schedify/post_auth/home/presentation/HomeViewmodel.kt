package com.unique.schedify.post_auth.home.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.AddScheduleItemDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListResponseDto
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.VerifyPinCodeResponseDto
import com.unique.schedify.post_auth.schedule_list.use_case.AddAddressCase
import com.unique.schedify.post_auth.schedule_list.use_case.UploadFileUseCase
import com.unique.schedify.post_auth.schedule_list.use_case.VerifyPinCodeCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val sharedPrefConfig: SharedPrefConfig,
    private val verifyPinCodeCase: VerifyPinCodeCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val addAddressCase: AddAddressCase,
): ViewModel() {

    private var _pinCodeValidationSuccess = mutableStateOf<Resource<VerifyPinCodeResponseDto.VerifyPinCodeResponseDtoItem>>(Resource.Default())
    val pinCodeValidationSuccess: State<Resource<VerifyPinCodeResponseDto.VerifyPinCodeResponseDtoItem>> = _pinCodeValidationSuccess


    fun logout() {
        sharedPrefConfig.clearAll()
    }

    fun verifyPinCode(pinCode: String) {
        _pinCodeValidationSuccess.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            with(verifyPinCodeCase.execute(pinCode)) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        Log.i("TaG", "success: ${this.data}")
//                        sharedPrefConfig.saveUserPincode(pinCode)
                        val data : VerifyPinCodeResponseDto.VerifyPinCodeResponseDtoItem = this.data[0]
                        _pinCodeValidationSuccess.value = Resource.Success(data)
                    }
                    is ApiResponseResource.Error -> {

                        Log.i("TaG", "error: ${this.errorMessage}")
                        _pinCodeValidationSuccess.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

    fun saveAddressInfo(addressInfo:  VerifyPinCodeResponseDto.VerifyPinCodeResponseDtoItem.PostOffice?) {
        sharedPrefConfig.saveUserPincode(addressInfo?.pinCode ?: "")
        sharedPrefConfig.saveUserCompleteAddress("${addressInfo?.name}, ${addressInfo?.district}, ${addressInfo?.state}, ${addressInfo?.country}, ${addressInfo?.pinCode}")
    }

    fun getUserCompleteAddress(): String {
        return sharedPrefConfig.getUserCompleteAddress() ?: ""
    }
}