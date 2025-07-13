package com.unique.schedify.core.presentation.download_and_save_ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.core.presentation.base_composables.BaseCompose
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.common_composables.ImageWithLoadingIndicator
import com.unique.schedify.core.presentation.download_and_save_ui.model.ConsentCardModel
import com.unique.schedify.core.presentation.download_and_save_ui.utility.PermissionState
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp18
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp4
import com.unique.schedify.core.presentation.utils.size_units.dp6
import com.unique.schedify.core.presentation.utils.size_units.sp14
import com.unique.schedify.core.presentation.utils.ui_utils.AppBaseGradients
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens

abstract class DownloadAndSaveConsents {
    abstract fun title(): String
    abstract fun imageUrl(): String
    abstract fun buttonText(): String
    abstract fun proceedToScreen(): AvailableScreens

    @Composable
    fun DownloadAndSaveConsentsScreen(
        navController: NavController,
        downloadAndSaveViewModel: DownloadAndSaveViewModel = hiltViewModel()
    ) {
        BaseCompose(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onSecondaryContainer),
            navController = navController,
            topBar = null,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(dp12),
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f),
                    contentAlignment = Alignment.Center
                ) {

                    ImageWithLoadingIndicator(
                        imageUrl = imageUrl(),
                        modifier = Modifier.fillMaxSize(0.8f)
                    )
                }

                Column(
                    Modifier.weight(0.7f)
                ) {
                    Text(title(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(dp18))

                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ConsentUi(
                            downloadAndSaveViewModel = downloadAndSaveViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ConsentUi(
        downloadAndSaveViewModel: DownloadAndSaveViewModel,
        navController: NavController
    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        // 🟡 Request permission dialog launcher
        val permissionLauncher: ManagedActivityResultLauncher<String, Boolean> = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if(isGranted.not()) {
                openAppNotificationSettings(context)

            } else {
                downloadAndSaveViewModel.onPermissionResult(isGranted)
            }
        }

        // 🟢 Handle ON_RESUME after settings
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_CREATE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        downloadAndSaveViewModel.checkIsNotificationPermissionEligible(true)
                        val granted = ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) ==
                                PackageManager.PERMISSION_GRANTED
                        downloadAndSaveViewModel.onPermissionResult(isGranted = granted)
                        downloadAndSaveViewModel.shouldShowNotificationSetting(isShow = granted.not())

                    } else {
                        downloadAndSaveViewModel.checkIsNotificationPermissionEligible(false)
                        Navigation.navigateToScreen(
                            navigateTo = proceedToScreen(),
                            navController = navController,
                        )
                    }
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val getContentCardModelConfigList = getConsentUiCardList(
                    context = context,
                    navController = navController,
                    downloadAndSaveViewModel = downloadAndSaveViewModel,
                    permissionLauncher = permissionLauncher
                )
                getContentCardModelConfigList.filter { it.visibility } .forEachIndexed { index, data ->
                    val trackLabelCount = index + 1
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(0.15f)
                        ) {
                            DottedLine(
                                color = if (data.isPermissionGranted) MaterialTheme.colorScheme.surfaceBright.copy(
                                    0.7f
                                ) else MaterialTheme.colorScheme.inversePrimary.copy(
                                    alpha = 0.3f
                                ),
                                dotRadius = dp2,
                                spaceBetween = dp6,
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier
                                    .border(
                                        width = dp1,
                                        color = if (data.isPermissionGranted) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.inversePrimary.copy(
                                            0.3f
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(dp6),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(dp24)
                                        .height(dp24)
                                        .background(
                                            color = if (data.isPermissionGranted) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.inversePrimary.copy(
                                                0.3f
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = trackLabelCount.toString(),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontSize = sp14,
                                            color = if(data.isPermissionGranted) MaterialTheme.colorScheme.onSecondaryContainer
                                            else MaterialTheme.colorScheme.inversePrimary
                                        )
                                    )
                                }
                            }

                            if (trackLabelCount == getContentCardModelConfigList.size) {
                                DottedLine(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    dotRadius = dp2,
                                    spaceBetween = dp6,
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                DottedLine(
                                    color = if (data.isPermissionGranted) MaterialTheme.colorScheme.surfaceBright.copy(
                                        0.7f
                                    ) else MaterialTheme.colorScheme.inversePrimary.copy(
                                        alpha = 0.3f
                                    ),
                                    dotRadius = dp2,
                                    spaceBetween = dp6,
                                    modifier = Modifier.weight(1f)
                                )
                            }


                        }

                        Card(
                            shape = RoundedCornerShape(dp12),
                            border = if(data.isCardButtonEnabled) BorderStroke(dp2, permissionStateColors(data.permissionState))
                            else BorderStroke(dp1, MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.3f)),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer),
                            elevation = CardDefaults.cardElevation(defaultElevation = dp6),
                            modifier = Modifier.weight(0.85f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(dp16),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(data.cardTitle, style = MaterialTheme.typography.titleLarge.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    ))
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(dp4))
                                    Text(
                                        data.cardSubTitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = permissionStateColors(data.permissionState)
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(dp16))

                                GradientButton(
                                    text = data.cardButtonText,
                                    btnGradient = permissionStateGradients(permissionState = data.permissionState),
                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    ),
                                    enabled = data.isCardButtonEnabled,
                                    onClick = data.onCardButtonClick
                                )
                            }
                        }
                    }
                    if(trackLabelCount == getContentCardModelConfigList.size) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(0.15f)
                            ) {
                                DottedLine(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    dotRadius = dp2,
                                    spaceBetween = dp6,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                DottedLine(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    dotRadius = dp2,
                                    spaceBetween = dp6,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.85f))
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(0.15f)
                            ) {
                                DottedLine(
                                    color = if (data.isPermissionGranted) MaterialTheme.colorScheme.primary.copy(
                                        0.7f
                                    ) else MaterialTheme.colorScheme.inversePrimary.copy(
                                        alpha = 0.7f
                                    ),
                                    dotRadius = dp2,
                                    spaceBetween = dp6,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "",
                                    tint = if(data.isPermissionGranted) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.inversePrimary.copy(
                                        alpha = 0.7f
                                    )
                                )
                                DottedLine(
                                    color = if (data.isPermissionGranted) MaterialTheme.colorScheme.surfaceBright.copy(
                                        0.7f
                                    ) else MaterialTheme.colorScheme.inversePrimary.copy(
                                        alpha = 0.7f
                                    ),
                                    dotRadius = dp2,
                                    spaceBetween = dp6,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.85f))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun permissionStateColors(permissionState: PermissionState): Color {
        return when(permissionState) {
            PermissionState.notRequested -> MaterialTheme.colorScheme.inversePrimary
            PermissionState.granted -> MaterialTheme.colorScheme.surfaceBright
            PermissionState.denied -> MaterialTheme.colorScheme.secondary
        }
    }

    @Composable
    private fun permissionStateGradients(permissionState: PermissionState): Brush {
        return when(permissionState) {
            PermissionState.notRequested -> AppBaseGradients.disabledBgGradient()
            PermissionState.granted -> AppBaseGradients.successBgGradient()
            PermissionState.denied -> AppBaseGradients.failureBgGradient()
        }
    }

    @Composable
    private fun getConsentUiCardList(
        context: Context,
        navController: NavController,
        downloadAndSaveViewModel: DownloadAndSaveViewModel,
        permissionLauncher: ManagedActivityResultLauncher<String, Boolean>
    ): List<ConsentCardModel> {
        val isNotificationPermissionEligible by rememberUpdatedState(downloadAndSaveViewModel.isNotificationPermissionEligible.value)
        val permissionGranted by rememberUpdatedState(downloadAndSaveViewModel.notificationPermissionGranted.value)
        val shouldShowSettings by rememberUpdatedState(downloadAndSaveViewModel.shouldShowSettings.value)
        val infoText = when {
            permissionGranted -> stringResource(R.string.permission_granted_proceed_to_next_step)
            shouldShowSettings -> stringResource(R.string.permission_denied_please_enable_manually_from_settings)
            else -> stringResource(R.string.require_notification_permission_to_notify)
        }

        return ArrayList<ConsentCardModel>().apply {
            add(
                ConsentCardModel(
                    visibility = isNotificationPermissionEligible,
                    cardTitle = stringResource(id = R.string.permission_request),
                    cardSubTitle = infoText,
                    cardButtonText = stringResource(id = R.string.request),
                    isPermissionGranted = permissionGranted,
                    permissionState = if(permissionGranted) PermissionState.granted else PermissionState.denied,
                    isCardButtonEnabled = isNotificationPermissionEligible && permissionGranted.not(),
                    onCardButtonClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val permissionStatus = ContextCompat.checkSelfPermission(
                                context,
                                POST_NOTIFICATIONS
                            )

                            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                downloadAndSaveViewModel.onPermissionResult(
                                    isGranted = true,
                                )
                                return@ConsentCardModel
                            }

                            permissionLauncher.launch(POST_NOTIFICATIONS)
                        }
                    }
                )
            )
            add(
                ConsentCardModel(
                    cardTitle = stringResource(id = R.string.data_preparation),
                    cardSubTitle = stringResource(id = R.string.download_and_prepare_data_for_you),
                    cardButtonText = stringResource(id = R.string.yup_continue),
                    isCardButtonEnabled = permissionGranted,
                    isPermissionGranted = permissionGranted,
                    permissionState = if(permissionGranted) PermissionState.granted else PermissionState.denied,
                    onCardButtonClick = {
                        Navigation.navigateToScreen(
                            navigateTo = proceedToScreen(),
                            navController = navController,
                        )
                    }
                )
            )
        }
    }

    private fun openAppNotificationSettings(context: Context) {
        val intent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                // Android 8.0+ (API 26+): Open app-specific notification settings
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            }
            else -> {
                // Fallback: open app settings screen
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:${context.packageName}".toUri()
                }
            }
        }

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Last resort fallback: open general settings
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    @Composable
    fun DottedLine(
        color: Color,
        dotRadius: Dp,
        spaceBetween: Dp,
        modifier: Modifier
    ) {
        Canvas(modifier = modifier
            .width(dp1)) {

            val dotPx = dotRadius.toPx()
            val spacePx = spaceBetween.toPx()
            var currentY = 0f

            while (currentY < size.height) {
                drawCircle(
                    color = color,
                    radius = dotPx,
                    center = Offset(size.width / 2, currentY + dotPx)
                )
                currentY += (dotPx * 2) + spacePx
            }
        }
    }
}