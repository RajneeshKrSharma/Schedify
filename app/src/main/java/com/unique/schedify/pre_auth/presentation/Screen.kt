package com.unique.schedify.pre_auth.presentation

sealed class Screen(val route: String) {
    data object AppTourScreen: Screen("app_tour_screen")
    data object LoginScreen: Screen("login_screen")
    data object HomeScreen: Screen("home_screen")
    data object PreAuthScreen: Screen("pre_auth")
    data object SplashScreen: Screen("splash")
    data object GroupListScreen: Screen("group_list_screen")
    data object GroupDetailedScreen: Screen("group_detailed_screen")
    data object SimpleScheduleList: Screen("schedule_list")
    data object AddCollaboratorScreen: Screen("add_collaborator_screen")
}