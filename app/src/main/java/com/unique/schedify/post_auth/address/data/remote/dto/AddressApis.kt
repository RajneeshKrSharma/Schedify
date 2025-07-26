package com.unique.schedify.post_auth.address.data.remote.dto

import com.unique.schedify.core.network.Api.ADD_ADDRESS
import com.unique.schedify.core.network.Api.FETCH_PIN_CODES
import com.unique.schedify.core.network.BaseApi
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface AddressApis: BaseApi {
    companion object {
        const val FETCH_PINCODES_API = FETCH_PIN_CODES
        const val ADD_ADDRESS_API = ADD_ADDRESS
    }

    @GET
    suspend fun fetchPincodes(
        @Url pincodeUrl: String
    ): Response<List<PincodeResponseDto>>

    @POST(ADD_ADDRESS_API)
    suspend fun addAddress(
        @Body addAddressRequestDto: AddAddressRequestDto
    ): Response<AddAddressResponseDto>
}