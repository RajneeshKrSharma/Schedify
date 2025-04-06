package com.unique.schedify.auth.login.domain.repository

import com.unique.schedify.auth.login.data.remote.dto.GetOtpResponseDto
import com.unique.schedify.auth.login.data.remote.LoginApis
import com.unique.schedify.auth.login.data.remote.dto.ConvertAccessTokenRequestDto
import com.unique.schedify.auth.login.data.remote.dto.ConvertAccessTokenResponseDto
import com.unique.schedify.auth.login.data.remote.dto.GetOtpRequestDto
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpRequestDto
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpResponseDto
import com.unique.schedify.auth.login.data.remote.dto.OAuthAccessTokenRequestDto
import com.unique.schedify.auth.login.data.remote.dto.OAuthAccessTokenResponseDto
import com.unique.schedify.auth.login.data.repository.LoginRepository
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(
    private val api: LoginApis,
) : LoginRepository {
    override suspend fun requestOtp(getOtpRequest: GetOtpRequestDto): Response<GetOtpResponseDto> {
        return api.getOtpResponse(getOtpRequest)
    }

    override suspend fun loginViaOtp(loginRequest: LoginViaOtpRequestDto): Response<LoginViaOtpResponseDto> {
        return api.getLoginViaOtpResponse(loginRequest)
    }

    override suspend fun oAuthAccessToken(oAuthAccessTokenRequestDto: OAuthAccessTokenRequestDto): Response<OAuthAccessTokenResponseDto> {
        return api.getOAuthAccessTokenResponse(oAuthAccessTokenRequestDto=oAuthAccessTokenRequestDto)
    }

    override suspend fun convertAccessToken(convertAccessTokenRequestDto: ConvertAccessTokenRequestDto): Response<ConvertAccessTokenResponseDto> {
        return api.getCovertAccessTokenResponse(convertAccessTokenRequestDto=convertAccessTokenRequestDto)
    }
}