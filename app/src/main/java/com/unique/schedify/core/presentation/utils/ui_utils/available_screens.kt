package com.unique.schedify.core.presentation.utils.ui_utils

import com.unique.schedify.pre_auth.presentation.Screen

sealed class AvailableScreens {
    sealed class PostAuth : AvailableScreens() {
        data object HomeScreen : PostAuth()
        data object ScheduleListScreen : PostAuth()
        data object SplitScheduleListScreen : PostAuth()
        data object SplitScheduleListDetailScreen : PostAuth()
        data object ExpenseScreen : PostAuth()
        data object PostAuthConsentScreen : PreAuth()
        data object PostAuthDownloadAndSaveScreen : PreAuth()
        data object UserMappedWeatherScreen : PreAuth()
    }

    sealed class PreAuth : AvailableScreens() {
        data object LoginScreen : PreAuth()
        data object SplashScreen : PreAuth()
        data object OtpInputScreen : PreAuth()
        data object PreAuthConsentScreen : PreAuth()
        data object PreAuthDownloadAndSaveScreen : PreAuth()
    }
}


fun AvailableScreens.toScreen(): Screen = when (this) {
    AvailableScreens.PostAuth.HomeScreen -> Screen.HomeScreen
    AvailableScreens.PostAuth.ScheduleListScreen -> Screen.SimpleScheduleList
    AvailableScreens.PostAuth.SplitScheduleListScreen -> Screen.GroupListScreen
    AvailableScreens.PostAuth.SplitScheduleListDetailScreen -> Screen.GroupDetailedScreen
    AvailableScreens.PreAuth.LoginScreen -> Screen.LoginScreen
    AvailableScreens.PreAuth.SplashScreen -> Screen.SplashScreen
    AvailableScreens.PostAuth.ExpenseScreen -> Screen.ExpenseScreen
    AvailableScreens.PreAuth.OtpInputScreen -> Screen.OtpInputScreen
    AvailableScreens.PreAuth.PreAuthConsentScreen -> Screen.PreAuthConsentScreen
    AvailableScreens.PreAuth.PreAuthDownloadAndSaveScreen -> Screen.PreAuthDownloadAndSaveScreen
    AvailableScreens.PostAuth.PostAuthConsentScreen -> Screen.PostAuthConsentScreen
    AvailableScreens.PostAuth.PostAuthDownloadAndSaveScreen -> Screen.PostAuthDownloadAndSaveScreen
    AvailableScreens.PostAuth.UserMappedWeatherScreen -> Screen.UserMappedWeatherScreen
}