package com.unique.schedify.post_auth.post_auth_loading.domain.use_case

import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.post_auth_loading.data.remote.dto.PostAuthDto
import com.unique.schedify.post_auth.post_auth_loading.domain.repository.PostAuthRepository
import com.unique.schedify.post_auth.post_auth_loading.data.remote.dto.UserMappedWeatherStatusDto
import javax.inject.Inject

class UserMappedWeatherStatusUseCase @Inject constructor(
    private val repository: PostAuthRepository,
) : ApiUseCase<ApiResponseResource<List<UserMappedWeatherStatusDto>>, String> {

    override suspend fun execute(args: String?): ApiResponseResource<List<UserMappedWeatherStatusDto>> {
        return (if (args != null) {
            return catchWrapper {repository.callUserMappedWeatherStatusApi(args)}
        } else {
            ApiResponseResource.Error("Invalid req.")
        })
    }
}

class GetPostAuthDetailsUseCase @Inject constructor(
    private val repository: PostAuthRepository,
) : ApiUseCase<ApiResponseResource<PostAuthDto>, Unit> {

    override suspend fun execute(args: Unit?): ApiResponseResource<PostAuthDto> {
        return catchWrapper {repository.callPostAuthApi()}
    }
}