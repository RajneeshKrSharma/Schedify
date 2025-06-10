package com.unique.schedify.post_auth.post_auth_loading.domain.repository

import com.unique.schedify.post_auth.post_auth_loading.data.remote.dto.PostAuthDto
import com.unique.schedify.post_auth.post_auth_loading.data.remote.dto.UserMappedWeatherStatusDto
import retrofit2.Response

interface PostAuthRepository {
    suspend fun callUserMappedWeatherStatusApi(pinCode: String): Response<List<UserMappedWeatherStatusDto>>

    suspend fun callPostAuthApi(): Response<PostAuthDto>
}