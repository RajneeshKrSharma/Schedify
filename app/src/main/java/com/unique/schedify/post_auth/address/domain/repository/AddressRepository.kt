package com.unique.schedify.post_auth.address.domain.repository

import com.unique.schedify.post_auth.address.data.remote.dto.AddAddressRequestDto
import com.unique.schedify.post_auth.address.data.remote.dto.AddAddressResponseDto
import com.unique.schedify.post_auth.address.data.remote.dto.PincodeResponseDto
import retrofit2.Response

interface AddressRepository {
    suspend fun fetchPincodes(pincode: String): Response<List<PincodeResponseDto>>

    suspend fun addAddress(addAddressRequestDto: AddAddressRequestDto): Response<AddAddressResponseDto>
}