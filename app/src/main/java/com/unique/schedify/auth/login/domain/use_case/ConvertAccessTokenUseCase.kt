package com.unique.schedify.auth.login.domain.use_case

import com.unique.schedify.auth.login.data.remote.dto.ConvertAccessTokenRequestDto
import com.unique.schedify.auth.login.data.remote.dto.ConvertAccessTokenResponseDto
import com.unique.schedify.auth.login.data.repository.LoginRepository
import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import javax.inject.Inject

class ConvertAccessTokenUseCase @Inject constructor(
    private val repository: LoginRepository,
) : ApiUseCase<ApiResponseResource<ConvertAccessTokenResponseDto>, ConvertAccessTokenRequestDto> {  // String is the argument type

    override suspend fun execute(args: ConvertAccessTokenRequestDto?): ApiResponseResource<ConvertAccessTokenResponseDto> {
        return (if (args != null) {
            val result = repository.convertAccessToken(args)
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

