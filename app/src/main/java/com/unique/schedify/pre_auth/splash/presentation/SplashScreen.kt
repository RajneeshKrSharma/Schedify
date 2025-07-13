package com.unique.schedify.pre_auth.splash.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.pre_auth.presentation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewmodel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        delay(1000)
        if (viewModel.isUserLoggedIn())
            Navigation.navigateToScreen(
                navigateTo = AvailableScreens.PostAuth.PostAuthConsentScreen,
                navController = navController,
                popUpToScreen = Screen.SplashScreen
            )

        else {
            Navigation.navigateToScreen(
                navigateTo = AvailableScreens.PreAuth.PreAuthConsentScreen,
                navController = navController,
                popUpToScreen = Screen.SplashScreen
            )

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(dp16),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {
            /*Image(
                painter = painterResource(id = R.drawable.schedify),
                contentDescription = "Logo",
            )*/
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}
