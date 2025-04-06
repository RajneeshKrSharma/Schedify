package com.unique.schedify.auth.login.data.remote

import com.unique.schedify.auth.login.data.remote.dto.ConvertAccessTokenRequestDto
import com.unique.schedify.auth.login.data.remote.dto.ConvertAccessTokenResponseDto
import com.unique.schedify.auth.login.data.remote.dto.GetOtpRequestDto
import com.unique.schedify.auth.login.data.remote.dto.GetOtpResponseDto
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpRequestDto
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpResponseDto
import com.unique.schedify.auth.login.data.remote.dto.OAuthAccessTokenRequestDto
import com.unique.schedify.auth.login.data.remote.dto.OAuthAccessTokenResponseDto
import com.unique.schedify.core.network.Api.CONVERT_ACCESS_TOKEN
import com.unique.schedify.core.network.Api.GET_OTP
import com.unique.schedify.core.network.Api.LOGIN_VIA_OTP
import com.unique.schedify.core.network.Api.OAUTH_ACCESS_TOKEN
import com.unique.schedify.core.network.BaseApi
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface LoginApis: BaseApi {
    @POST(GET_OTP)
    suspend fun getOtpResponse(
        @Body getOtpRequestDto: GetOtpRequestDto
    ): Response<GetOtpResponseDto>

    @POST(LOGIN_VIA_OTP)
    suspend fun getLoginViaOtpResponse(
        @Body loginRequest: LoginViaOtpRequestDto
    ): Response<LoginViaOtpResponseDto>

    @POST
    suspend fun getOAuthAccessTokenResponse(
        @Url url : String = OAUTH_ACCESS_TOKEN,
        @Body oAuthAccessTokenRequestDto: OAuthAccessTokenRequestDto
    ): Response<OAuthAccessTokenResponseDto>

    @POST(CONVERT_ACCESS_TOKEN)
    suspend fun getCovertAccessTokenResponse(
        @Body convertAccessTokenRequestDto: ConvertAccessTokenRequestDto
    ): Response<ConvertAccessTokenResponseDto>
}