package com.unique.schedify.post_auth.post_auth_loading.data.repository

import com.unique.schedify.post_auth.post_auth_loading.data.remote.PostAuthApis
import com.unique.schedify.post_auth.post_auth_loading.data.remote.dto.PostAuthDto
import com.unique.schedify.post_auth.post_auth_loading.data.remote.dto.UserMappedWeatherStatusDto
import com.unique.schedify.post_auth.post_auth_loading.domain.repository.PostAuthRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostAuthRepositoryImpl @Inject constructor(
    private val postAuthApis: PostAuthApis
): PostAuthRepository {
    override suspend fun callUserMappedWeatherStatusApi(pinCode: String): Response<List<UserMappedWeatherStatusDto>> {
        return postAuthApis.getUserMappedWeatherData(pinCode = pinCode)
    }

    override suspend fun callPostAuthApi(): Response<PostAuthDto> {
        return postAuthApis.callPostAuthApi()
    }
}