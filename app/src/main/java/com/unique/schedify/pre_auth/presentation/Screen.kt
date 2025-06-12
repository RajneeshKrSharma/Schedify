package com.unique.schedify.pre_auth.presentation

sealed class Screen(val route: String) {
    data object AppTourScreen: Screen("app_tour_screen")
    data object LoginScreen: Screen("login_screen")
    data object HomeScreen: Screen("home_screen")
    data object SplashScreen: Screen("splash")
    data object GroupListScreen: Screen("group_list_screen")
    data object GroupDetailedScreen: Screen("group_detailed_screen")
    data object SimpleScheduleList: Screen("schedule_list")
    data object AddCollaboratorScreen: Screen("add_collaborator_screen")
    data object ExpenseScreen: Screen("expense_screen")
    data object OtpInputScreen: Screen("otp_input_screen")
    data object PreAuthConsentScreen: Screen("pre_auth_consent_screen")
    data object PreAuthDownloadAndSaveScreen: Screen("pre_auth_download_and_save_screen")
    data object PostAuthConsentScreen: Screen("post_auth_consent_screen")
    data object PostAuthDownloadAndSaveScreen: Screen("post_auth_download_and_save_screen")
    data object UserMappedWeatherScreen: Screen("user_mapped_weather_screen")
}