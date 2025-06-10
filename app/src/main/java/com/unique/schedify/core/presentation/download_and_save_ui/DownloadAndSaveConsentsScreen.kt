package com.unique.schedify.core.presentation.download_and_save_ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.core.app.ActivityCompat
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
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp150
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp18
import com.unique.schedify.core.presentation.utils.size_units.dp2
import com.unique.schedify.core.presentation.utils.size_units.dp200
import com.unique.schedify.core.presentation.utils.size_units.dp24
import com.unique.schedify.core.presentation.utils.size_units.dp36
import com.unique.schedify.core.presentation.utils.size_units.dp50
import com.unique.schedify.core.presentation.utils.size_units.dp6
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.dp80
import com.unique.schedify.core.presentation.utils.size_units.sp14
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
                        style = MaterialTheme.typography.titleLarge.copy(
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
        val activity = context as Activity
        val lifecycleOwner = LocalLifecycleOwner.current

        val isNotificationPermissionEligible by rememberUpdatedState(downloadAndSaveViewModel.isNotificationPermissionEligible.value)
        val stepNumber by rememberUpdatedState(downloadAndSaveViewModel.stepNumber.value)
        val permissionGranted by rememberUpdatedState(downloadAndSaveViewModel.notificationPermissionGranted.value)
        val permissionRequested by rememberUpdatedState(downloadAndSaveViewModel.permissionRequested.value)
        val shouldShowSettings by rememberUpdatedState(downloadAndSaveViewModel.shouldShowSettings.value)
        val inSettingsFlow by rememberUpdatedState(downloadAndSaveViewModel.permissionInSettingsFlow.value)

        // 🟡 Request permission dialog launcher
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            val shouldShowRationale = activity.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.shouldShowRequestPermissionRationale(it, POST_NOTIFICATIONS)
                } else {
                    // For versions below Android 13, permission is implicitly granted
                    false
                }
            } ?: false

            downloadAndSaveViewModel.onPermissionResult(isGranted, shouldShowRationale)
        }

        // 🟢 Handle ON_RESUME after settings
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME || event == Lifecycle.Event.ON_CREATE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        downloadAndSaveViewModel.checkIsNotificationPermissionEligible(true)
                        val granted = ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) ==
                                PackageManager.PERMISSION_GRANTED
                        if(inSettingsFlow) {
                            val shouldShowRationale = activity.let {
                                ActivityCompat.shouldShowRequestPermissionRationale(it, POST_NOTIFICATIONS)
                            } ?: false

                            downloadAndSaveViewModel.onPermissionResult(granted, shouldShowRationale)
                        } else {
                            if (granted) {
                                downloadAndSaveViewModel.onPermissionResult(
                                    isGranted = true,
                                    shouldShowRationale = false
                                )
                            }
                        }

                    } else {
                        downloadAndSaveViewModel.checkIsNotificationPermissionEligible(false)
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
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(isNotificationPermissionEligible) {
                    DottedLine(
                        color = if (permissionGranted) MaterialTheme.colorScheme.primary.copy(
                            0.7f
                        ) else MaterialTheme.colorScheme.inversePrimary.copy(
                            alpha = 0.7f
                        ),
                        dotRadius = dp2,
                        spaceBetween = dp6,
                        height = dp80
                    )
                    Spacer(Modifier.padding(vertical = dp2))
                    Box(
                        modifier = Modifier
                            .border(
                                width = dp1,
                                color = if (permissionGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary.copy(
                                    0.6f
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
                                    color = if (permissionGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary.copy(
                                        0.3f
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "1", // will correct later with dynamic code
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = sp14,
                                    color = if(permissionGranted) MaterialTheme.colorScheme.onSecondaryContainer
                                    else MaterialTheme.colorScheme.inversePrimary
                                )
                            )
                        }
                    }
                    Spacer(Modifier.padding(vertical = dp2))
                    DottedLine(
                        color = if (permissionGranted) MaterialTheme.colorScheme.primary.copy(
                            0.7f
                        ) else MaterialTheme.colorScheme.inversePrimary.copy(
                            0.7f
                        ),
                        dotRadius = dp2,
                        spaceBetween = dp6,
                        height = dp150
                    )
                    Spacer(Modifier.padding(vertical = dp2))
                    DottedLine(
                        color = if (permissionGranted) MaterialTheme.colorScheme.primary.copy(
                            0.7f
                        ) else MaterialTheme.colorScheme.inversePrimary.copy(
                            0.7f
                        ),
                        dotRadius = dp2,
                        spaceBetween = dp6,
                        height = dp50
                    )
                    Spacer(Modifier.padding(vertical = dp2))
                    Box(
                        modifier = Modifier
                            .border(
                                width = dp1,
                                color = if (permissionGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary.copy(
                                    0.6f
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
                                    color = if (permissionGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary.copy(
                                        0.3f
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "2", // will correct later with dynamic code
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = sp14,
                                    color = if(permissionGranted) MaterialTheme.colorScheme.onSecondaryContainer
                                    else MaterialTheme.colorScheme.inversePrimary
                                )
                            )
                        }
                    }
                } else {
                    DottedLine(
                        color = if (permissionGranted) MaterialTheme.colorScheme.primary.copy(
                            0.7f
                        ) else MaterialTheme.colorScheme.inversePrimary.copy(
                            0.7f
                        ),
                        dotRadius = dp2,
                        spaceBetween = dp6,
                        height = dp80
                    )
                    Spacer(Modifier.padding(vertical = dp2))
                    Box(
                        modifier = Modifier
                            .border(
                                width = dp1,
                                color = MaterialTheme.colorScheme.primary,
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
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "1", // will correct later with dynamic code
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontSize = sp14,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                }
            }

            Column(
                modifier =  Modifier
                    .weight(0.85f)
                    .padding(horizontal = dp16),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if(isNotificationPermissionEligible) {
                    // 🔐 Permission Card
                    Card(
                        shape = RoundedCornerShape(dp12),
                        border = if(permissionGranted) BorderStroke(dp2, MaterialTheme.colorScheme.onSecondary) else {
                            if (stepNumber == 1) {
                                BorderStroke(dp2, MaterialTheme.colorScheme.secondary)
                            } else null
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = dp6),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dp200)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(dp16),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    stringResource(R.string.permission_request),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ))
                                Spacer(Modifier.height(dp8))

                                val infoText = when {
                                    permissionGranted -> stringResource(R.string.permission_granted_proceed_to_next_step)
                                    shouldShowSettings -> stringResource(R.string.permission_denied_please_enable_manually_from_settings)
                                    permissionRequested -> stringResource(R.string.permission_denied_please_request_again)
                                    else -> stringResource(R.string.require_notification_permission_to_notify_on_scheduled_item_s_weather_status)
                                }

                                Text(
                                    infoText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            GradientButton(
                                text = if (permissionGranted) stringResource(R.string.granted) else stringResource(
                                    R.string.request
                                ),
                                btnGradient = if (permissionGranted) null else Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                                    )
                                ),
                                enabled = !permissionGranted,
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    color =  MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val permissionStatus = ContextCompat.checkSelfPermission(
                                        context,
                                        POST_NOTIFICATIONS
                                    )

                                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                        // Permission already granted - no action needed
                                        downloadAndSaveViewModel.onPermissionResult(
                                            isGranted = true,
                                            shouldShowRationale = false
                                        )
                                        return@GradientButton
                                    }

                                    val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity,
                                        POST_NOTIFICATIONS
                                    )

                                    if (showRationale) {
                                        downloadAndSaveViewModel.resetRequestState()
                                        permissionLauncher.launch(POST_NOTIFICATIONS)
                                    } else {
                                        downloadAndSaveViewModel.markSettingsFlowStarted()
                                        openAppNotificationSettings(context)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(dp36))
                    Card(
                        shape = RoundedCornerShape(dp12),
                        border = if(permissionGranted) BorderStroke(dp2, MaterialTheme.colorScheme.onSecondary) else {
                            if (stepNumber == 2) {
                                BorderStroke(dp2, MaterialTheme.colorScheme.secondary)
                            } else null
                        },
                        colors = if(permissionGranted) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer) else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onTertiaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = dp6),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dp200)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(dp16),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    stringResource(R.string.data_preparation), style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ))
                                Spacer(Modifier.height(dp8))
                                Text(
                                    stringResource(R.string.download_and_prepare_data_for_you),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                                    )
                                )
                            }

                            GradientButton(
                                text = stringResource(R.string.yup_continue),
                                btnGradient = if(permissionGranted) null else Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.secondary.copy(alpha=0.5f)
                                    )
                                ),
                                enabled = permissionGranted,
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    color =  MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Navigation.navigateToScreen(
                                    navigateTo = proceedToScreen(),
                                    navController = navController,
                                )
                            }
                        }
                    }

                } else {
                    Card(
                        shape = RoundedCornerShape(dp12),
                        border = BorderStroke(dp2, MaterialTheme.colorScheme.onSecondary),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer),
                        elevation = CardDefaults.cardElevation(defaultElevation = dp6),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dp200)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(dp16),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(stringResource(R.string.data_preparation), style = MaterialTheme.typography.headlineSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ))
                                Spacer(Modifier.height(dp8))
                                Text(
                                    stringResource(R.string.download_prepare_data_for_you),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                )
                            }

                            GradientButton(
                                text = stringResource(R.string.yup_continue),
                                textStyle = MaterialTheme.typography.titleMedium.copy(
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            ) {
                                Navigation.navigateToScreen(
                                    navigateTo = proceedToScreen(),
                                    navController = navController,
                                )
                            }
                        }
                    }
                }
            }
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
        height: Dp
    ) {
        Canvas(modifier = Modifier
            .width(dp1)
            .height(height)) {

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