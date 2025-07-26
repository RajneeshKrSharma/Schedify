package com.unique.schedify.post_auth.address.data.repository

import com.unique.schedify.post_auth.address.data.remote.dto.AddAddressRequestDto
import com.unique.schedify.post_auth.address.data.remote.dto.AddAddressResponseDto
import com.unique.schedify.post_auth.address.data.remote.dto.AddressApis
import com.unique.schedify.post_auth.address.data.remote.dto.PincodeResponseDto
import com.unique.schedify.post_auth.address.domain.repository.AddressRepository
import retrofit2.Response
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val addressApis: AddressApis
) : AddressRepository {
    override suspend fun fetchPincodes(pincode: String): Response<List<PincodeResponseDto>> {
        return addressApis.fetchPincodes("${AddressApis.FETCH_PINCODES_API}$pincode")
    }

    override suspend fun addAddress(addAddressRequestDto: AddAddressRequestDto): Response<AddAddressResponseDto> {
        return addressApis.addAddress(addAddressRequestDto = addAddressRequestDto)
    }
}