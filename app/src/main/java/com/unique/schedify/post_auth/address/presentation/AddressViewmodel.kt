package com.unique.schedify.post_auth.address.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unique.schedify.R
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.core.util.Resource
import com.unique.schedify.post_auth.address.data.remote.dto.AddAddressRequestDto
import com.unique.schedify.post_auth.address.data.remote.dto.AddAddressResponseDto
import com.unique.schedify.post_auth.address.data.remote.model.PincodeUiConfig
import com.unique.schedify.post_auth.address.domain.use_case.AddAddressUseCase
import com.unique.schedify.post_auth.address.domain.use_case.FetchPincodesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddressViewmodel @Inject constructor(
    private val fetchPincodesUseCase: FetchPincodesUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val sharedPrefConfig: SharedPrefConfig
) : ViewModel() {

    private var _pincodeApiDataState =
        mutableStateOf<Resource<PincodeUiConfig>>(Resource.Default())
    val pincodeApiDataState: State<Resource<PincodeUiConfig>> = _pincodeApiDataState

    private var _pincodeApiDataCopyState =
        mutableStateOf<Resource<PincodeUiConfig>>(Resource.Default())

    private val _isSearchingInProgressState = mutableStateOf(false)
    val isSearchingInProgressState: State<Boolean> = _isSearchingInProgressState

    private var _addAddressState =
        mutableStateOf<Resource<AddAddressResponseDto>>(Resource.Default())
    val addAddressState: State<Resource<AddAddressResponseDto>> = _addAddressState

    val pincodeInputByUserState = mutableStateOf("")

    val isSearchBoxSectionVisibleState = mutableStateOf(false)

    fun fetchPincodes(pincode: String) {
        viewModelScope.launch {
            _pincodeApiDataState.value = Resource.Loading()

            with(fetchPincodesUseCase.execute(args = pincode)) {
                when (this) {
                    is ApiResponseResource.Error -> {
                        _pincodeApiDataState.value = Resource.Error(message = this.errorMessage)
                    }

                    is ApiResponseResource.Success -> {
                        this.data.firstOrNull()?.let { pincodeResponse ->
                            pincodeResponse.postOffice?.let { postOfficeList ->
                                if (!pincodeResponse.message.isNullOrEmpty() && postOfficeList.isNotEmpty()) {
                                    val responseData =
                                        Resource.Success(
                                            PincodeUiConfig(
                                                message = "Got ${
                                                    pincodeResponse.message.split(":")
                                                        .lastOrNull() ?: ""
                                                } results, ",
                                                postOfficeList = postOfficeList.mapNotNull { postOfficeData ->
                                                    postOfficeData?.run {
                                                        PincodeUiConfig.PostOfficeConfig(
                                                            name = name.orEmpty(),
                                                            country = country.orEmpty(),
                                                            district = district.orEmpty(),
                                                            region = region.orEmpty(),
                                                            block = block.orEmpty(),
                                                            division = division.orEmpty(),
                                                            state = state.orEmpty(),
                                                            pincode = pincode
                                                        )
                                                    }
                                                }
                                            )
                                        )

                                    _pincodeApiDataCopyState.value = responseData
                                    _pincodeApiDataState.value = responseData

                                } else {
                                    _pincodeApiDataState.value = Resource.Error(stringResourceId = R.string.no_data_available)
                                }

                            } ?: let {
                                _pincodeApiDataState.value =
                                    pincodeResponse.message?.let { errorMsg ->
                                        Resource.Error(message = errorMsg)
                                    } ?: Resource.Error(stringResourceId = R.string.no_data_available)
                            }

                        } ?: let {
                            _pincodeApiDataState.value = Resource.Error(stringResourceId = R.string.no_data_available)
                        }
                    }
                }
            }
        }
    }

    fun addAddress(postOfficeConfig: PincodeUiConfig.PostOfficeConfig) {
        viewModelScope.launch {
            _addAddressState.value = Resource.Loading()
            with(
                addAddressUseCase.execute(
                    AddAddressRequestDto(
                        pinCode = postOfficeConfig.pincode,
                        address = postOfficeConfig.run {
                            "Name: $name | " +
                            "Block: $block | " +
                                    "Region: $region | " +
                                    "District: $district | " +
                                    "Division: $division | " +
                                    "State: $state | " +
                                    "Country: $country"
                        }
                    )
                )
            ) {
                when (this) {
                    is ApiResponseResource.Error -> {
                        _addAddressState.value = Resource.Error(message = this.errorMessage)
                    }

                    is ApiResponseResource.Success -> {
                        with(this.data) {
                            pincode?.let { pincodeNotNull ->
                                address?.let { addressNotNull ->
                                    (pincodeNotNull to addressNotNull)
                                }
                            }?.let { (pincode, address) ->
                                sharedPrefConfig.savePinCode(pinCode = pincode)
                                sharedPrefConfig.saveAddress(address = address)
                                _addAddressState.value = Resource.Success(this)
                            } ?: let { _addAddressState.value = Resource.Error(stringResourceId = R.string.something_went_wrong_with_pincode_address) }
                        }
                    }
                }
            }
        }
    }

    fun searchAddressData(query: String) {
        _pincodeApiDataState.value = Resource.Loading()

        // get operation is only performed using _pincodeApiDataCopyState data
        val pincodeApiDataCopiedData = _pincodeApiDataCopyState.value.data
        val originalAddressDataList = pincodeApiDataCopiedData?.postOfficeList ?: emptyList()
        if(query.isNotEmpty() && originalAddressDataList.isNotEmpty()) {
            val searchedData = originalAddressDataList.filter { data ->
                data.name.lowercase(Locale.getDefault())
                    .contains(query.lowercase(Locale.getDefault()))
            }
            _pincodeApiDataState.value = Resource.Success(
                PincodeUiConfig(
                    message = "Got ${searchedData.size} results with $query, ",
                    postOfficeList = searchedData
                )
            )
        } else {
            _pincodeApiDataState.value = Resource.Success(
                pincodeApiDataCopiedData
            )
        }
    }

    fun searchingInProgress(isSearchingInProgress: Boolean) {
        _isSearchingInProgressState.value = isSearchingInProgress
    }

    fun resetAllState() {
        _pincodeApiDataCopyState.value = Resource.Default()
        _pincodeApiDataState.value = Resource.Default()
        _addAddressState.value = Resource.Default()
    }

    fun resetAddressState() {
        _addAddressState.value = Resource.Default()
    }
}