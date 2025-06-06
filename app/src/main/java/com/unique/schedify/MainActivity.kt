package com.unique.schedify

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.unique.schedify.auth.login.presentation.LoginScreen
import com.unique.schedify.auth.login.presentation.LoginViewmodel
import com.unique.schedify.auth.login.presentation.OtpInputScreen
import com.unique.schedify.core.presentation.utils.GROUP_GRAPH_NAME
import com.unique.schedify.core.presentation.utils.LOGIN_GRAPH_NAME
import com.unique.schedify.post_auth.home.presentation.HomeScreen
import com.unique.schedify.post_auth.home.presentation.HomeViewmodel
import com.unique.schedify.post_auth.home.presentation.TakeAddressScreen
import com.unique.schedify.post_auth.schedule_list.data.remote.dto.ScheduleListResponseDto
import com.unique.schedify.post_auth.schedule_list.presentation.AddScheduleScreen
import com.unique.schedify.post_auth.schedule_list.presentation.ScheduleDetailScreen
import com.unique.schedify.post_auth.schedule_list.presentation.SimpleScheduleListScreen
import com.unique.schedify.post_auth.schedule_list.presentation.SimpleScheduleListViewModel
import com.unique.schedify.post_auth.split_expense.data.remote.dto.GroupExpenseResponseDto
import com.unique.schedify.post_auth.split_expense.presentation.ExpenseScreen
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
    @RequiresApi(Build.VERSION_CODES.O)
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

                    composable(route = Screen.HomeScreen.route) {
                        HomeScreen(navController = navController)
                    }
                    composable(route = Screen.TakeAddressScreen.route) {
                        val homeViewModel: HomeViewmodel = hiltViewModel()
                        TakeAddressScreen(navController, homeViewModel)
                    }

                    navigation(
                        startDestination = Screen.LoginScreen.route,
                        route = LOGIN_GRAPH_NAME
                    ) {
                        composable(Screen.LoginScreen.route) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(LOGIN_GRAPH_NAME)
                            }
                            val loginViewmodel: LoginViewmodel = hiltViewModel(parentEntry)
                            LoginScreen(
                                loginViewmodel,
                                context = this@MainActivity,
                                navController = navController
                            )
                        }

                        composable(
                            route = Screen.OtpInputScreen.route
                        ) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(LOGIN_GRAPH_NAME)
                            }
                            val loginViewmodel: LoginViewmodel = hiltViewModel(parentEntry)
                            OtpInputScreen(loginViewmodel, navController = navController)
                        }
                    }

                    navigation(
                        startDestination = Screen.GroupListScreen.route,
                        route = GROUP_GRAPH_NAME
                    ) {
                        composable(Screen.GroupListScreen.route) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(GROUP_GRAPH_NAME)
                            }
                            val splitExpenseViewModel: SplitExpenseViewModel =
                                hiltViewModel(parentEntry)
                            GroupListScreen(navController, splitExpenseViewModel)
                        }

                        composable(
                            route = "${Screen.GroupDetailedScreen.route}/{groupJson}",
                            arguments = listOf(navArgument("groupJson") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->

                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(GROUP_GRAPH_NAME)
                            }
                            val splitExpenseViewModel: SplitExpenseViewModel =
                                hiltViewModel(parentEntry) // Shared ViewModel

                            val groupJsonEncoded = backStackEntry.arguments?.getString("groupJson")
                                ?: return@composable
                            val groupJson = URLDecoder.decode(
                                groupJsonEncoded,
                                StandardCharsets.UTF_8.toString()
                            )
                            val groupItem =
                                Gson().fromJson(groupJson, GroupExpenseResponseDto::class.java)

                            LoadCollaboratorScreen(navController, groupItem, splitExpenseViewModel)
                        }

                        composable(
                            route = "${Screen.ExpenseScreen.route}/{collaboratorJson}",
                            arguments = listOf(navArgument("collaboratorJson") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(GROUP_GRAPH_NAME)
                            }
                            val splitExpenseViewModel: SplitExpenseViewModel =
                                hiltViewModel(parentEntry)
                            val collaboratorJsonEncoded =
                                backStackEntry.arguments?.getString("collaboratorJson")
                                    ?: return@composable
                            val collaboratorJsonDecoded = URLDecoder.decode(
                                collaboratorJsonEncoded,
                                StandardCharsets.UTF_8.toString()
                            )
                            val collaboratorItem = Gson().fromJson(
                                collaboratorJsonDecoded,
                                GroupExpenseResponseDto.Collaborator::class.java
                            )
                            ExpenseScreen(
                                navController = navController,
                                collaborator = collaboratorItem,
                                splitExpenseViewModel = splitExpenseViewModel
                            )
                        }
                    }

                    composable(route = Screen.SimpleScheduleList.route) {
                        val scheduleViewModel: SimpleScheduleListViewModel = hiltViewModel()
                        SimpleScheduleListScreen(
                            navController = navController,
                            scheduleViewModel
                        )

                    }

                    composable(
                        route = "schedule_detail/{item}",
                        arguments = listOf(navArgument("item") {
                            type = NavType.StringType
                        }) // ✅ Ensure it's treated as a string
                    ) { backStackEntry ->
                        val json = backStackEntry.arguments?.getString("item") ?: return@composable
                        val scheduleItem = Gson().fromJson(
                            Uri.decode(json),
                            ScheduleListResponseDto.Data::class.java
                        ) // ✅ Decode safely

                        ScheduleDetailScreen(
                            item = scheduleItem,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    navigation(
                        startDestination = Screen.SimpleScheduleList.route,
                        route = GROUP_GRAPH_NAME
                    ) {


                        composable(route = Screen.SimpleScheduleList.route) { backStackEntry ->
//                            val scheduleViewModel: SimpleScheduleListViewModel = hiltViewModel()
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(GROUP_GRAPH_NAME)
                            }
                            val scheduleViewModel: SimpleScheduleListViewModel = hiltViewModel(parentEntry)
                            SimpleScheduleListScreen(navController, scheduleViewModel)
                        }

                        composable(
                            route = "add_schedule?locationType={locationType}",
                            arguments = listOf(
                                navArgument("locationType") { defaultValue = "current" },
                            )
                        ) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(GROUP_GRAPH_NAME)
                            }
                            val scheduleViewModel: SimpleScheduleListViewModel = hiltViewModel(parentEntry)
                            val locationType =
                            backStackEntry.arguments?.getString("locationType") ?: "new"

                            AddScheduleScreen(
                                navController,
                                scheduleViewModel,
                                locationType = locationType
                            )
                        }
                    }



                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("FCM", "onResume: ")
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}