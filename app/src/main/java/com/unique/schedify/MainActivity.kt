package com.unique.schedify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.unique.schedify.core.presentation.utils.GROUP_GRAPH_NAME
import com.unique.schedify.post_auth.home.presentation.HomeScreen
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.presentation.AddCollaboratorScreen
import com.unique.schedify.post_auth.split_expense.presentation.GroupListScreen
import com.unique.schedify.post_auth.split_expense.presentation.LoadCollaboratorScreen
import com.unique.schedify.post_auth.split_expense.presentation.SplitExpenseViewModel
import com.unique.schedify.pre_auth.pre_auth_loading.presentation.PreAuthScreen
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
                    composable(route = Screen.PreAuthScreen.route) {
                        PreAuthScreen(navController = navController)
                    }
                    composable(route = Screen.LoginScreen.route) {
                        LoginScreen(navController = navController, context = this@MainActivity)
                    }
                    composable(route = Screen.HomeScreen.route) {
                        HomeScreen(navController = navController)
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
                            val groupItem = Gson().fromJson(groupJson, GroupExpenseResponseDto.Data::class.java)

                            LoadCollaboratorScreen(navController, groupItem, splitExpenseViewModel)
                        }
                        composable(route = Screen.AddCollaboratorScreen.route) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(GROUP_GRAPH_NAME)
                            }
                            val splitExpenseViewModel: SplitExpenseViewModel = hiltViewModel(parentEntry)
                            AddCollaboratorScreen(navController, splitExpenseViewModel)
                        }
                    }

                    composable(route = Screen.SimpleScheduleList.route) {

                    }
                }
            }
        }
    }
}