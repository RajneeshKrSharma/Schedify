package com.unique.schedify.auth.login.domain.use_case

import com.unique.schedify.auth.login.data.remote.dto.OAuthAccessTokenRequestDto
import com.unique.schedify.auth.login.data.remote.dto.OAuthAccessTokenResponseDto
import com.unique.schedify.auth.login.data.repository.LoginRepository
import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import javax.inject.Inject

class OAuthAccessTokenUseCase @Inject constructor(
    private val repository: LoginRepository,
) : ApiUseCase<ApiResponseResource<OAuthAccessTokenResponseDto>, OAuthAccessTokenRequestDto> {  // String is the argument type

    override suspend fun execute(args: OAuthAccessTokenRequestDto?): ApiResponseResource<OAuthAccessTokenResponseDto> {
        return (if (args != null) {
            val result = repository.oAuthAccessToken((args))
            if (result.isSuccessful) {
                result.body()?.let { data ->
                    ApiResponseResource.Success(data)
                } ?: ApiResponseResource.Error("")

            } else {
                ApiResponseResource.Error(result.errorBody()?.string() ?: "Something went wrong with error body")
            }
        } else {
            ApiResponseResource.Error("Invalid req.")
        })
    }
}

