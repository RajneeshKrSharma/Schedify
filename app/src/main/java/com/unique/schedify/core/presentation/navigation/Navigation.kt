package com.unique.schedify.core.presentation.navigation

import androidx.navigation.NavController
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.presentation.utils.ui_utils.toScreen
import com.unique.schedify.pre_auth.presentation.Screen

object Navigation {

    fun navigateToScreen(
        navigateTo: AvailableScreens,
        navController: NavController,
    ) {
        navController.navigate(navigateTo.toScreen().route)
    }

    fun navigateToScreenWithArgs(
        navigateTo: AvailableScreens,
        navController: NavController,
        args: Any
    ) {
        navController.navigate("${navigateTo.toScreen().route}/$args")
    }

    fun navigateToScreen(
        navigateTo: AvailableScreens,
        navController: NavController,
        popUpToScreen: Screen
    ) {

        navController.navigate(navigateTo.toScreen().route) {
            popUpTo(popUpToScreen.route) {
                inclusive = true
            }
        }
    }


    fun navigateAndClearBackStackScreen(
        navigateTo: AvailableScreens,
        navController: NavController,
    ) {
        navController.navigate(navigateTo.toScreen().route) {
            popUpTo(0) { inclusive = true }
        }
    }
}
