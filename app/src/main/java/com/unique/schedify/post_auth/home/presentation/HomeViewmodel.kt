package com.unique.schedify.post_auth.home.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.presentation.common_composables.CellUiDetails
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.util.toArgbColorInt
import com.unique.schedify.core.util.toGradientColors
import com.unique.schedify.pre_auth.pre_auth_loading.data.local.PreAuthDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(
    private val sharedPrefConfig: SharedPrefConfig,
    private val preAuthDao: PreAuthDao
): ViewModel() {

    private val _homeListCellDetails = MutableStateFlow<List<CellUiDetails>>(emptyList())
    val homeListCellDetails =
        _homeListCellDetails
            .onStart { getHomeListData() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    private val _homeCarouselListCellDetails = MutableStateFlow<List<CellUiDetails>>(emptyList())
    val homeCarouselListCellDetails =
        _homeCarouselListCellDetails
            .onStart { getCarouselListData() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )



    fun logout() {
        sharedPrefConfig.clearAll()
    }

    fun getUserName(): String =
        (sharedPrefConfig.getAuthUserEmailId()
            ?.split("@")?.firstOrNull()
            ?: "Hi User")


    private fun getHomeListData() {
        viewModelScope.launch {
            preAuthDao.getHomeCellDetail().let { homeListCellDetails ->
                homeListCellDetails.map { detail ->
                    with(detail) {
                        CellUiDetails(
                            text = title ?: "NA",
                            description = description,
                            screen = AvailableScreens.PostAuth.SplitScheduleListScreen,
                            imageUrl = imageUrl,
                            backgroundCardGradientColors = (detail.colorLight to detail.colorDark).toGradientColors(),
                            titleColor = titleColor.toArgbColorInt()?.let { Color(it) }
                        )
                    }
                }.let { homeCellUiDetails ->
                    _homeListCellDetails.value = homeCellUiDetails
                }
            }
        }
    }

    private fun getCarouselListData() {
        viewModelScope.launch {
            preAuthDao.getHomeCarouselBannerData().let { homeCarouselBannerDetails ->
                homeCarouselBannerDetails.map { detail ->
                    with(detail) {
                        CellUiDetails(
                            text = title ?: "NA",
                            screen = AvailableScreens.PostAuth.ScheduleListScreen,
                            imageUrl = imageUrl,
                            backgroundCardGradientColors = (detail.colorLight to detail.colorDark).toGradientColors(),
                        )
                    }
                }.let { homeCellUiDetails ->
                    _homeCarouselListCellDetails.value = homeCellUiDetails
                }
            }
        }
    }
}