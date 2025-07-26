package com.unique.schedify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.gson.Gson
import com.unique.schedify.auth.login.presentation.LoginScreen
import com.unique.schedify.auth.login.presentation.LoginViewmodel
import com.unique.schedify.auth.login.presentation.OtpInputScreen
import com.unique.schedify.core.presentation.utils.GROUP_GRAPH_NAME
import com.unique.schedify.core.presentation.utils.LOGIN_GRAPH_NAME
import com.unique.schedify.post_auth.address.presentation.PincodeScreen
import com.unique.schedify.post_auth.home.presentation.HomeScreen
import com.unique.schedify.post_auth.post_auth_loading.PostAuthConsentUiScreen
import com.unique.schedify.post_auth.post_auth_loading.PostAuthDownloadAndSaveUiScreen
import com.unique.schedify.post_auth.schedule_list.presentation.ScheduleListScreen
import com.unique.schedify.post_auth.schedule_list.presentation.ScheduleListViewModel
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.presentation.ExpenseScreen
import com.unique.schedify.post_auth.split_expense.presentation.GroupListScreen
import com.unique.schedify.post_auth.split_expense.presentation.LoadCollaboratorScreen
import com.unique.schedify.post_auth.split_expense.presentation.SplitExpenseViewModel
import com.unique.schedify.post_auth.user_mapped_weather.presentation.UserMappedWeatherScreen
import com.unique.schedify.pre_auth.pre_auth_loading.presentation.PreAuthConsentUiScreen
import com.unique.schedify.pre_auth.pre_auth_loading.presentation.PreAuthDownloadAndSaveUiScreen
import com.unique.schedify.pre_auth.presentation.Screen
import com.unique.schedify.pre_auth.splash.presentation.SplashScreen
import com.unique.schedify.ui.theme.SchedifyTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchedifyTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.SplashScreen.route
                ) {
                    composable(route = Screen.SplashScreen.route) {
                        SplashScreen(navController = navController)
                    }
                    composable(route = Screen.AppTourScreen.route) {
                        SplashScreen(navController = navController)
                    }

                    composable(route = Screen.HomeScreen.route) {
                        HomeScreen(navController = navController)
                    }

                    navigation(startDestination = Screen.LoginScreen.route, route = LOGIN_GRAPH_NAME) {
                        composable(
                            Screen.LoginScreen.route,
                            exitTransition = { slideOutHorizontally(
                                targetOffsetX = { -it },
                                animationSpec = tween(durationMillis = 500)
                            ) },
                            popEnterTransition = { slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(durationMillis = 500)
                            ) }
                        ) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(LOGIN_GRAPH_NAME)
                            }
                            val loginViewmodel: LoginViewmodel = hiltViewModel(parentEntry)
                            LoginScreen(loginViewmodel, context = this@MainActivity, navController = navController)
                        }

                        composable(
                            route = Screen.OtpInputScreen.route,
                            exitTransition = {
                                slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(durationMillis = 500)
                            ) },
                            popEnterTransition = {
                                slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(durationMillis = 500)
                            ) }
                        ) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(LOGIN_GRAPH_NAME)
                            }
                            val loginViewmodel: LoginViewmodel = hiltViewModel(parentEntry)
                            OtpInputScreen(loginViewmodel, navController = navController)
                        }
                    }

                    navigation(startDestination = Screen.GroupListScreen.route, route = GROUP_GRAPH_NAME) {
                        composable(Screen.GroupListScreen.route) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(GROUP_GRAPH_NAME)
                            }
                            val splitExpenseViewModel: SplitExpenseViewModel = hiltViewModel(parentEntry)
                            GroupListScreen(navController, splitExpenseViewModel)
                        }

                        composable(
                            route = "${Screen.GroupDetailedScreen.route}/{groupJson}",
                            arguments = listOf(navArgument("groupJson") { type = NavType.StringType })
                        ) { backStackEntry ->

                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(GROUP_GRAPH_NAME)
                            }
                            val splitExpenseViewModel: SplitExpenseViewModel = hiltViewModel(parentEntry) // Shared ViewModel

                            val groupJsonEncoded = backStackEntry.arguments?.getString("groupJson") ?: return@composable
                            val groupJson = URLDecoder.decode(groupJsonEncoded, StandardCharsets.UTF_8.toString())
                            val groupItem = Gson().fromJson(groupJson, GroupExpenseResponseDto::class.java)

                            LoadCollaboratorScreen(navController, groupItem, splitExpenseViewModel)
                        }

                        composable(
                            route = "${Screen.ExpenseScreen.route}/{collaboratorJson}",
                            arguments = listOf(navArgument("collaboratorJson") { type = NavType.StringType })) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(GROUP_GRAPH_NAME)
                            }
                            val splitExpenseViewModel: SplitExpenseViewModel = hiltViewModel(parentEntry)
                            val collaboratorJsonEncoded = backStackEntry.arguments?.getString("collaboratorJson") ?: return@composable
                            val collaboratorJsonDecoded = URLDecoder.decode(collaboratorJsonEncoded, StandardCharsets.UTF_8.toString())
                            val collaboratorItem = Gson().fromJson(collaboratorJsonDecoded, GroupExpenseResponseDto.Collaborator::class.java)
                            ExpenseScreen(
                                navController = navController,
                                collaborator = collaboratorItem,
                                splitExpenseViewModel = splitExpenseViewModel
                            )
                        }
                    }

                    composable(route = Screen.PreAuthConsentScreen.route) {
                        PreAuthConsentUiScreen(navController = navController)
                    }
                    composable(route = Screen.PreAuthDownloadAndSaveScreen.route) {
                        PreAuthDownloadAndSaveUiScreen(navController = navController)
                    }
                    composable(route = Screen.PostAuthConsentScreen.route) {
                        PostAuthConsentUiScreen(navController = navController)
                    }
                    composable(route = Screen.PostAuthDownloadAndSaveScreen.route) {
                        PostAuthDownloadAndSaveUiScreen(navController = navController)
                    }
                    composable(route = Screen.UserMappedWeatherScreen.route) {
                        UserMappedWeatherScreen(navController = navController)
                    }

                    // Schedule List Section
                    composable(route = Screen.ScheduleListItem.route) {
                        val scheduleListViewModel : ScheduleListViewModel = hiltViewModel()
                        ScheduleListScreen(
                            navController,
                            scheduleListViewModel
                        )
                    }

                    composable(route = Screen.PincodeScreen.route) {
                        PincodeScreen(
                            navController,
                        )
                    }
                }
            }
        }
    }
}