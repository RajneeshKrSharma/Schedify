package com.unique.schedify.post_auth.schedule_list.data.remote.dto



import com.unique.schedify.core.network.Api.SCHEDULE_LIST
import com.unique.schedify.core.network.Api.UPLOAD_ATTACHMENTS
import com.unique.schedify.core.network.Api.ADD_ADDRESS
import com.unique.schedify.core.network.Api.GET_STATUS
import com.unique.schedify.core.network.BaseApi
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddAddressRequestDto
import com.unique.schedify.post_auth.schedule_list.data.remote.model.AddScheduleItemRequestModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url


interface ScheduleListApis : BaseApi {


    @GET(SCHEDULE_LIST)
    suspend fun getScheduleList(): Response<ScheduleListResponseDto>

    @POST(SCHEDULE_LIST)
    suspend fun addScheduleItem(
        @Body req: AddScheduleItemRequestModel?
    ): Response<AddScheduleItemDto>

    @GET
    suspend fun verifyPinCode(@Url fullUrl: String): Response<VerifyPinCodeResponseDto>

    @Multipart
    @POST(UPLOAD_ATTACHMENTS)
    suspend fun uploadFiles(
        @Part("schedule_id") scheduleId: Int,
        @Part files: List<MultipartBody.Part>
    ): Response<AttachedFileResponseDto>

    @POST(ADD_ADDRESS)
    suspend fun addAddress(
        @Body req: AddAddressRequestDto?
    ): Response<AddAddressResponseDto>

    @GET(GET_STATUS)
    suspend fun getWeatherStatus(
        @Query("pincode") pinCode: String,
        @Query("scheduledItemId") scheduleItemId: String,
        @Query("notifyMedium") notifyMedium: String = "PUSH_NOTIFICATION"
    ) : Response<WeatherStatusResponseDto>
}
