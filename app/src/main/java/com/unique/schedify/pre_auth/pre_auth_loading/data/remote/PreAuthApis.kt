package com.unique.schedify.pre_auth.pre_auth_loading.data.remote

import com.unique.schedify.core.network.Api.PRE_APP_DETAILS
import com.unique.schedify.core.network.BaseApi
import com.unique.schedify.pre_auth.pre_auth_loading.data.remote.dto.PreAuthInfoDto
import retrofit2.Response
import retrofit2.http.GET

interface PreAuthApis: BaseApi {
    @GET(PRE_APP_DETAILS)
    suspend fun getPreAppDetails(): Response<PreAuthInfoDto>
}