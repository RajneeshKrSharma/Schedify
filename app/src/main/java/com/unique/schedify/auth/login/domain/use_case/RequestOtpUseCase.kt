package com.unique.schedify.auth.login.domain.use_case

import com.unique.schedify.auth.login.data.remote.dto.GetOtpRequestDto
import com.unique.schedify.auth.login.data.remote.dto.GetOtpResponseDto
import com.unique.schedify.auth.login.data.repository.LoginRepository
import com.unique.schedify.core.ApiUseCase
import com.unique.schedify.core.util.ApiResponseResource
import javax.inject.Inject

class RequestOtpUseCase @Inject constructor(
    private val repository: LoginRepository,
) : ApiUseCase<ApiResponseResource<GetOtpResponseDto>, GetOtpRequestDto> {  // String is the argument type

    override suspend fun execute(args: GetOtpRequestDto?): ApiResponseResource<GetOtpResponseDto> {
        return (if (args != null) {
            return catchWrapper {repository.requestOtp((args))}
        } else {
            ApiResponseResource.Error("Invalid req.")
        })
    }
}

