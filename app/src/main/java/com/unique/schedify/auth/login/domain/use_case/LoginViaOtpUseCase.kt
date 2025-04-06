package com.unique.schedify.auth.login.domain.use_case

import com.google.gson.Gson
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpRequestDto
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpResponseDto
import com.unique.schedify.auth.login.data.repository.LoginRepository
import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import javax.inject.Inject

class LoginViaOtpUseCase @Inject constructor(
    private val repository: LoginRepository,
) : ApiUseCase<ApiResponseResource<LoginViaOtpResponseDto>, LoginViaOtpRequestDto> {

    override suspend fun execute(args: LoginViaOtpRequestDto?): ApiResponseResource<LoginViaOtpResponseDto> {
        return (if (args != null) {
            val result = repository.loginViaOtp((args))

            if (result.isSuccessful) {
                result.body()?.let { data ->
                    ApiResponseResource.Success(data)
                } ?: ApiResponseResource.Error("")

            } else {
                val errorBody = result.errorBody()?.string() ?: ""
                if (errorBody.isNotEmpty()) {
                    ApiResponseResource.Error(Gson().fromJson(errorBody, LoginViaOtpResponseDto::class.java).message ?: "Something went wrong with error body")
                } else
                    ApiResponseResource.Error("Something went wrong with (error body is empty)")

            }
        } else {
            ApiResponseResource.Error("Invalid req.")
        })
    }
}

