package com.unique.schedify.auth.login.domain.use_case

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
            catchWrapper { repository.loginViaOtp((args)) }
        } else {
            ApiResponseResource.Error("Invalid req.")
        })
    }
}

