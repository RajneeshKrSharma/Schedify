package com.unique.schedify.core.presentation.utils.ui_utils

import com.unique.schedify.pre_auth.presentation.Screen

sealed class AvailableScreens {
    sealed class PostAuth : AvailableScreens() {
        data object HomeScreen : PostAuth()
        data object ScheduleListScreen : PostAuth()
        data object SplitScheduleListScreen : PostAuth()
        data object SplitScheduleListDetailScreen : PostAuth()
        data object AddCollaboratorScreen : PostAuth()
    }

    sealed class PreAuth : AvailableScreens() {
        data object PreAuthScreen : PreAuth()
        data object LoginScreen : PreAuth()
        data object SplashScreen : PreAuth()
    }
}


fun AvailableScreens.toScreen(): Screen = when (this) {
    AvailableScreens.PostAuth.HomeScreen -> Screen.HomeScreen
    AvailableScreens.PostAuth.ScheduleListScreen -> Screen.SimpleScheduleList
    AvailableScreens.PostAuth.SplitScheduleListScreen -> Screen.GroupListScreen
    AvailableScreens.PostAuth.SplitScheduleListDetailScreen -> Screen.GroupDetailedScreen
    AvailableScreens.PreAuth.PreAuthScreen -> Screen.PreAuthScreen
    AvailableScreens.PreAuth.LoginScreen -> Screen.LoginScreen
    AvailableScreens.PreAuth.SplashScreen -> Screen.SplashScreen
    AvailableScreens.PostAuth.AddCollaboratorScreen -> Screen.AddCollaboratorScreen
}