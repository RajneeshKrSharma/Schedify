package com.unique.schedify.post_auth.post_auth_loading.data.remote

import com.unique.schedify.core.network.Api.POST_AUTH_DATA
import com.unique.schedify.core.network.Api.USER_MAPPED_WEATHER_DATA
import com.unique.schedify.core.network.BaseApi
import com.unique.schedify.post_auth.post_auth_loading.data.remote.dto.PostAuthDto
import com.unique.schedify.post_auth.post_auth_loading.data.remote.dto.UserMappedWeatherStatusDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PostAuthApis: BaseApi {
    companion object {
        const val PINCODE = "pincode"
    }

    @GET(POST_AUTH_DATA)
    suspend fun callPostAuthApi(): Response<PostAuthDto>

    @GET(USER_MAPPED_WEATHER_DATA)
    suspend fun getUserMappedWeatherData(
        @Query(PINCODE) pinCode: String
    ): Response<List<UserMappedWeatherStatusDto>>
}