package com.unique.schedify.post_auth.address.domain.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.address.data.remote.dto.AddAddressRequestDto
import com.unique.schedify.post_auth.address.data.remote.dto.AddAddressResponseDto
import com.unique.schedify.post_auth.address.domain.repository.AddressRepository
import javax.inject.Inject

class AddAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository,
): ApiUseCase<ApiResponseResource<AddAddressResponseDto>, AddAddressRequestDto> {
    override suspend fun execute(args: AddAddressRequestDto?): ApiResponseResource<AddAddressResponseDto> {
        return (args?.let { addAddressRequestDto ->
            catchWrapper {
                addressRepository.addAddress(addAddressRequestDto = addAddressRequestDto)
            }

        } ?: ApiResponseResource.Error("Invalid req.") )
    }
}