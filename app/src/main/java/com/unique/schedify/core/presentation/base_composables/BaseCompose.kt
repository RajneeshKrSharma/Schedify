package com.unique.schedify.core.presentation.base_composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.network.utility.NetworkErrorEmitter
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.post_auth.home.presentation.HomeViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseCompose(
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = {
        TopAppBar(
            title = { Text(text = stringResource(R.string.app_name)) },
            modifier = Modifier.padding(dp16),
        )
    },
    bottomBar: @Composable (() -> Unit)? = null,
    contentModifier: Modifier = Modifier,
    stripModifier: Modifier = Modifier,
    navController: NavController,
    brush: Brush? = null,
    content: @Composable (paddingValues: PaddingValues) -> Unit,
) {
    // Observe the connectivity state using collectAsState
    val viewModel: BaseViewModel = hiltViewModel()
    val isConnected by viewModel.isConnected.collectAsState()
    val showStrip by viewModel.showStrip.collectAsState()

    Scaffold(
        topBar = {
            topBar?.invoke()
        },
        bottomBar = { bottomBar?.invoke() },
        content = { paddingValues ->
            Box(modifier = modifier
                .fillMaxSize()
                .let { if (brush != null) it.background(brush) else it }
            ) {
                Column(
                    modifier = contentModifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    content(paddingValues)
                }

                // Animated Connectivity Strip with slide animations
                AnimatedVisibility(
                    visible = showStrip,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 500)
                    ) + fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(durationMillis = 500)
                    ) + fadeOut(animationSpec = tween(durationMillis = 500)),
                    modifier = stripModifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = paddingValues.calculateBottomPadding())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isConnected) MaterialTheme.colorScheme.primary else
                                    MaterialTheme.colorScheme.secondary
                            )
                            .padding(dp2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isConnected) stringResource(R.string.back_online) else stringResource(
                                R.string.no_internet_connection
                            ),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    )

    ObserveGlobalNetworkErrors(
        navController = navController
    )
}

@Composable
fun ObserveGlobalNetworkErrors(
    navController: NavController,
    homeViewModel: HomeViewmodel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        NetworkErrorEmitter.networkErrors.collect { errorCode ->
            when (errorCode) {
                401 -> {
                    homeViewModel.logout()
                    Navigation.navigateAndClearBackStackScreen(
                        navigateTo = AvailableScreens.PreAuth.LoginScreen,
                        navController = navController
                    )
                }
                else -> {}
            }
        }
    }
}



