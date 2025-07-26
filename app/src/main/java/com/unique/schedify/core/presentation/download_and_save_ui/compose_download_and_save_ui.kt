package com.unique.schedify.core.presentation.download_and_save_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import com.unique.schedify.core.presentation.common_composables.CircularGradientProgressBar
import com.unique.schedify.core.presentation.common_composables.ErrorUi
import com.unique.schedify.core.presentation.download_and_save_ui.model.PostLoginPreRequisitesEvents
import com.unique.schedify.core.presentation.download_and_save_ui.utility.DownloadWorkerStatus
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.sp14
import com.unique.schedify.core.presentation.utils.size_units.sp16
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import kotlinx.coroutines.delay

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

        LaunchedEffect(Unit) {
            downloadAndSaveViewModel.postLoginPreRequisiteEvents.collect { event ->
                when (event) {
                    PostLoginPreRequisitesEvents.NavigateToPincode -> {
                        Navigation.navigateAndClearBackStackScreen(
                            navigateTo = AvailableScreens.PostAuth.PincodeScreen,
                            navController = navController
                        )
                    }

                    PostLoginPreRequisitesEvents.NavigateToProceedScreen -> {
                        Navigation.navigateAndClearBackStackScreen(
                            navigateTo = proceedToScreen(),
                            navController = navController
                        )
                    }
                }
            }
        }

        LaunchedEffect(dataReceived.value) {
            if (dataReceived.value.data.size == apisToCall().size) {
                delay(500)
                downloadAndSaveViewModel.isApplicableForPreRequisites()
            }
        }

        BaseCompose(
            modifier = Modifier.background(MaterialTheme.colorScheme.onSecondaryContainer),
            topBar = topAppBar,
            navController = navController,
        ) {
            when(dataReceived.value.status) {
                DownloadWorkerStatus.DONE, DownloadWorkerStatus.INIT -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dp16),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
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
                        Spacer(modifier = Modifier.padding(vertical = dp16))
                        Box(
                            modifier = Modifier
                                .wrapContentSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Box(modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(0.3f)
                                .wrapContentHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularGradientProgressBar(
                                    modifier = Modifier.fillMaxSize(),
                                    progress = (dataReceived.value.data.size.toFloat() / apisToCall().size),
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.SpaceEvenly,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.syncing_in_progress),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = sp14,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                )
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "${dataReceived.value.data.size}",
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    )
                                    Text(
                                        stringResource(R.string.slash_separator),
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        )
                                    )
                                    Text(
                                        text = "${apisToCall().size}",
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                DownloadWorkerStatus.FAILED -> {
                    ErrorUi(
                        message = dataReceived.value.errorMsg,
                        onRetry = {
                            downloadAndSaveViewModel.startWorker(
                                apisToCall = apisToCall(),
                                tag = workerTagName()
                            )
                        }
                    )
                }
            }
            content?.invoke()
        }
    }
}