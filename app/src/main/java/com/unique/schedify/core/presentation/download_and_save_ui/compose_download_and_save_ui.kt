package com.unique.schedify.core.presentation.download_and_save_ui

import DynamicLottieAnimation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.common_composables.GradientProgressBar
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp40
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp14
import com.unique.schedify.core.presentation.utils.size_units.sp16
import com.unique.schedify.core.presentation.utils.size_units.sp20
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens

abstract class DownloadAndSaveUi {
    abstract fun title() : String
    abstract fun apisToCall() : List<String>
    abstract fun proceedToScreen(): AvailableScreens
    abstract fun workerTagName() : String

    @Composable
    fun ComposeDownloadAndSaveUi(
        downloadAndSaveViewModel: DownloadAndSaveViewModel = hiltViewModel(),
        navController: NavController,
        topAppBar: @Composable (() -> Unit)?,
        content: @Composable (() -> Unit)? = null
    ) {

        val dataReceived = downloadAndSaveViewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            downloadAndSaveViewModel.startWorker(
                apisToCall = apisToCall(),
                tag = workerTagName()
            )
        }

        LaunchedEffect(dataReceived.value) {
            if (dataReceived.value.size == apisToCall().size) {
                Navigation.navigateAndClearBackStackScreen(
                    navigateTo = proceedToScreen(),
                    navController = navController
                )
            }
        }

        BaseCompose(
            modifier = Modifier.background(MaterialTheme.colorScheme.onSecondaryContainer),
            topBar = topAppBar,
            navController = navController,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dp16),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {

                DynamicLottieAnimation(
                    rawResId = R.raw.download_and_sync,
                    modifier = Modifier.weight(0.4f)
                )
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = dp16, top = dp16),
                        text = title(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = sp16,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dp40),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.syncing_in_progress),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = sp14,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        )
                        Spacer(Modifier.height(dp8))
                        GradientProgressBar(
                            progress = (dataReceived.value.size.toFloat() / apisToCall().size)
                        )
                        Spacer(Modifier.height(dp8))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "${dataReceived.value.size}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = sp16,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            )
                            Text(
                                stringResource(R.string.slash_separator),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = sp20,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            )
                            Text(
                                text = "${apisToCall().size}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = sp16,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            )
                        }
                    }
                }
            }
            content?.invoke()
        }
    }
}