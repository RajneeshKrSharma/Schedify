package com.unique.schedify.post_auth.schedule_list.data.remote.dto

import com.unique.schedify.core.network.Api.PIN_CODE_VERIFY
import com.unique.schedify.core.network.Api.SCHEDULE_LIST
import com.unique.schedify.core.network.BaseApi
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddScheduleItemRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ScheduleListApis : BaseApi {


    @GET(SCHEDULE_LIST)
    suspend fun getScheduleList(): Response<ScheduleListResponseDto>

    @POST(SCHEDULE_LIST)
    suspend fun addScheduleItem(
        @Body req: AddScheduleItemRequestModel?
    ): Response<AddScheduleItemDto>

    @GET("${PIN_CODE_VERIFY}{pincode}")
    suspend fun verifyPinCode(@Path("pincode") pinCode: String?): Response<VerifyPinCodeResponseDto>
}
