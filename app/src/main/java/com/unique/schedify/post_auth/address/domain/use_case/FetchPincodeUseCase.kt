package com.unique.schedify.post_auth.address.domain.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.address.data.remote.dto.PincodeResponseDto
import com.unique.schedify.post_auth.address.domain.repository.AddressRepository
import javax.inject.Inject

class FetchPincodesUseCase @Inject constructor(
    private val addressRepository: AddressRepository,
): ApiUseCase<ApiResponseResource<List<PincodeResponseDto>>, String> {
    override suspend fun execute(args: String?): ApiResponseResource<List<PincodeResponseDto>> {
        return (args?.let { pincode ->
            catchWrapper {
                addressRepository.fetchPincodes(pincode = pincode)
            }

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}