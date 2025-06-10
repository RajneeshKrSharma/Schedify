package com.unique.schedify.core.presentation.download_and_save_ui.utility

import androidx.work.ListenableWorker.Result
import androidx.work.workDataOf
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.post_auth.post_auth_loading.data.remote.dto.UserMappedWeatherStatusDto
import com.unique.schedify.post_auth.post_auth_loading.domain.use_case.GetPostAuthDetailsUseCase
import com.unique.schedify.post_auth.post_auth_loading.domain.use_case.UserMappedWeatherStatusUseCase
import com.unique.schedify.post_auth.post_auth_loading.local.ScheduleItem
import com.unique.schedify.post_auth.post_auth_loading.local.UserMappedWeatherStatus
import com.unique.schedify.post_auth.post_auth_loading.local.UserMappedWeatherStatusDao
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.HomeCarouselBannerEntity
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.HomeCellDetailEntity
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.PreAuthDao
import com.unique.schedify.pre_auth.pre_auth_loading.domain.use_case.GetPreAuthDetails
import com.unique.schedify.pre_auth.pre_auth_utils.toGradientColors
import javax.inject.Inject

interface ApiHandler {
    suspend fun callApi(apiName: String): Result
}

class PreAuthApiHandler @Inject constructor(
    private val getPreAuthDetails: GetPreAuthDetails,
    private val preAuthDao: PreAuthDao
) : ApiHandler {
    override suspend fun callApi(apiName: String): Result {
        return when (apiName) {
            PreAuthDownloadApis.PRE_AUTH_DETAIL.name -> {
                return when (val response = getPreAuthDetails.execute()) {
                    is ApiResponseResource.Success -> {
                        val data = response.data

                        val prepareHomeCarouselBannerData = data.homeCarouselBanners
                            ?.filterNotNull()
                            ?.mapNotNull { homeCarouselBanner ->
                                with(homeCarouselBanner) {
                                    val safeId = id ?: return@mapNotNull null
                                    val gradientColors = backgroundGradientColors?.toGradientColors()

                                    HomeCarouselBannerEntity(
                                        id = safeId,
                                        title = title,
                                        subtitle = subtitle,
                                        imageUrl = imageUrl,
                                        colorLight = gradientColors?.colorLight,
                                        colorDark = gradientColors?.colorDark
                                    )
                                }
                            }


                        if(prepareHomeCarouselBannerData.isNullOrEmpty().not()) {
                            preAuthDao.deleteHomeCellDetail()
                            preAuthDao.insertHomeCarouselBannerData(prepareHomeCarouselBannerData ?: emptyList())
                        }

                        val prepareHomeCellDetail = data.homeCellDetails
                            ?.filterNotNull()
                            ?.mapNotNull { homeCellDetail ->
                                with(homeCellDetail) {
                                    val gradientColors = backgroundGradientColors?.toGradientColors()
                                    val safeId = id ?: return@mapNotNull null

                                    HomeCellDetailEntity(
                                        id = safeId,
                                        title = title,
                                        description = description,
                                        imageUrl = imageUrl,
                                        colorLight = gradientColors?.colorLight,
                                        colorDark = gradientColors?.colorDark,
                                        titleColor = titleColor
                                    )
                                }
                            }


                        if(prepareHomeCellDetail.isNullOrEmpty().not()) {
                            preAuthDao.deleteHomeCellDetail()
                            preAuthDao.insertHomeCellDetail(prepareHomeCellDetail ?: emptyList())
                        }

                        Result.success(workDataOf("api" to apiName, "status" to "done"))
                    }

                    is ApiResponseResource.Error -> Result.failure()
                }
            }
            else -> Result.failure()
        }
    }
}

class PostAuthApiHandler @Inject constructor(
    private val getPostAuthDetailsUseCase: GetPostAuthDetailsUseCase,
    private val userMappedWeatherStatusUseCase: UserMappedWeatherStatusUseCase,
    private val userMappedWeatherStatusDao: UserMappedWeatherStatusDao,
    private val sharedPrefConfig: SharedPrefConfig
) : ApiHandler {
    override suspend fun callApi(apiName: String): Result {
        return when (apiName) {
            PostAuthDownloadApis.POST_AUTH_DETAIL.name -> {
                return(when (val response = getPostAuthDetailsUseCase.execute()) {
                    is ApiResponseResource.Success -> {
                        val data = response.data
                        data.addressDetail?.pincode?.let { pincode ->
                            sharedPrefConfig.savePinCode(pinCode = pincode)
                        }

                        Result.success(workDataOf("api" to apiName, "status" to "done"))
                    }
                    is ApiResponseResource.Error -> {
                        Result.failure()
                    }
                })
            }
            PostAuthDownloadApis.USER_MAPPED_WEATHER_STATUS_DETAILS.name -> {
                val getUserPinCode = sharedPrefConfig.getPinCode()
                getUserPinCode?.let { pincode ->
                    return when (val response = userMappedWeatherStatusUseCase.execute(args = pincode)) {
                        is ApiResponseResource.Success -> {
                            val data: List<UserMappedWeatherStatusDto> = response.data

                            val mappedList: List<UserMappedWeatherStatus> = data.map { dto ->
                                with(dto) {
                                    UserMappedWeatherStatus(
                                        id = id,
                                        uniqueKey = uniqueKey,
                                        pinCode = pincode,
                                        forecastTime = forecastTime,
                                        timeStamp = timeStamp,
                                        weatherType = weatherType,
                                        weatherDescription = weatherDescription,
                                        temperatureCelsius = temperatureCelsius,
                                        humidityPercent = humidityPercent,
                                        updatedCount = updatedCount,
                                        notifyCount = notifyCount,
                                        nextNotifyIn = nextNotifyIn,
                                        nextNotifyAt = nextNotifyAt,
                                        notifyMedium = notifyMedium,
                                        isActive = isActive,
                                        lastUpdated = lastUpdated,
                                        scheduleItem = scheduleItem?.let {
                                            ScheduleItem(
                                                id = it.id,
                                                dateTime = it.dateTime,
                                                title = it.title,
                                                lastScheduleOn = it.lastScheduleOn,
                                                isWeatherNotifyEnabled = it.isWeatherNotifyEnabled,
                                                isItemPinned = it.isItemPinned,
                                                subTitle = it.subTitle,
                                                isArchived = it.isArchived,
                                                priority = it.priority,
                                                userId = it.userId,
                                                googleAuthUserId = it.googleAuthUserId
                                            )
                                        }
                                    )
                                }
                            }
                            userMappedWeatherStatusDao.deleteUserMappedWeatherStatusData()
                            userMappedWeatherStatusDao.insertUserMappedWeatherStatusDaoData(mappedList)
                            Result.success(workDataOf("api" to apiName, "status" to "done"))
                        }

                        is ApiResponseResource.Error -> {
                            Result.failure()
                        }
                    }
                } ?: Result.success(workDataOf("api" to apiName, "status" to "done | no pincode"))
            }
            else -> Result.failure()
        }
    }
}
