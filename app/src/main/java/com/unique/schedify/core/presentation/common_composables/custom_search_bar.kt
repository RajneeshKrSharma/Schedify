package com.unique.schedify.core.presentation.common_composables

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.unique.schedify.R
import com.unique.schedify.core.presentation.utils.size_units.*
import com.unique.schedify.core.util.animations.MicRipple
import com.unique.schedify.core.util.findActivity
import com.unique.schedify.core.util.permissions.PermissionDenialCauseEnum
import com.unique.schedify.core.util.permissions.PermissionHelper
import com.unique.schedify.core.util.permissions.PermissionStatus
import com.unique.schedify.core.util.permissions.RECORD_AUDIO_PERMISSION
import com.unique.schedify.core.util.permissions.permissionRequestLauncher
import com.unique.schedify.core.util.voice.VoiceRecognizerHelper
import kotlinx.coroutines.delay

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    hint: String = stringResource(R.string.search),
    onSearch: (String) -> Unit,
    onMicClicked: () -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val screenWidth = with(density) { windowInfo.containerSize.width.toDp() }
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    var isMicOpened by remember { mutableStateOf(false) }
    var isUserSpeaking by remember { mutableStateOf(false) }

    val voiceRecognizer: VoiceRecognizerHelper = remember {
        VoiceRecognizerHelper(
            context = context,
            onSpeechStateChanged = { isUserSpeaking = it },
            onResult = {
                isMicOpened = false
                query = it
                onSearch(it)
            },
            onError = {
                isMicOpened = false
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        )
    }

    val showDeniedDialog = remember { mutableStateOf(false) }

    if(showDeniedDialog.value)
        CustomDialog(
            title = "Regarding Permission",
            desc = "Microphone permission was denied. To use this feature, enable it manually in settings.",
            confirmButtonText = stringResource(R.string.enable),
            dismissButtonText = stringResource(R.string.cancel),
            onConfirm = {
                showDeniedDialog.value = false
                PermissionHelper.openAppSettings(context = context)
            },
            onDismiss = {
                showDeniedDialog.value = false
            }
        )

    val micPermissionRequestLauncher = permissionRequestLauncher(
        permission = RECORD_AUDIO_PERMISSION,
        activity = context.findActivity(),
        onPermissionStatus = { status ->
            when (status) {
                is PermissionStatus.PermissionAction -> {
                    if (status.isPermissionAllowed) {
                        isMicOpened = true
                        voiceRecognizer.startListening()
                        Log.d("Rajneesh", "PincodeScreen: isPermissionAllowed -> ${status.isPermissionAllowed}")
                    }
                }
                is PermissionStatus.PermissionDenialCause -> {
                    when (status.permissionDenialCauseEnum) {
                        PermissionDenialCauseEnum.DENIED_TEMPORARY -> {
                            Log.d("Rajneesh", "PincodeScreen: PermissionDenialCauseEnum.DENIED_TEMPORARY")
                        }
                        PermissionDenialCauseEnum.DENIED_PERMANENTLY -> {
                            showDeniedDialog.value = true
                            Log.d("Rajneesh", "PincodeScreen: PermissionDenialCauseEnum.DENIED_PERMANENTLY")

                        }
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        delay(300)
        isExpanded = true
    }

    val transition = updateTransition(targetState = isExpanded, label = "search-bar-transition")

    val width by transition.animateDp(
        transitionSpec = {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        },
        label = "width"
    ) { expanded -> if (expanded) screenWidth else dp40 }

    val cornerRadius by transition.animateDp(
        transitionSpec = {
            spring(dampingRatio = Spring.DampingRatioLowBouncy)
        },
        label = "corner-radius"
    ) { if (it) dp12 else dp16 }

    Surface(
        modifier = modifier
            .height(dp56)
            .width(width)
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                dp1,
                MaterialTheme.colorScheme.onTertiaryContainer,
                RoundedCornerShape(cornerRadius)
            )
            .background(MaterialTheme.colorScheme.onSecondaryContainer)
            .clickable { isExpanded = true },
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = dp1
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dp8),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(300)) + expandHorizontally(),
                exit = fadeOut(tween(200)) + shrinkHorizontally()
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        when {
                            isUserSpeaking -> {
                                Text(
                                    stringResource(R.string.listening),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            isMicOpened -> {
                                Text(
                                    stringResource(R.string.start_speaking),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            else -> {
                                TextField(
                                    value = query,
                                    onValueChange = {
                                        query = it
                                        onSearch(it)
                                    },
                                    placeholder = {
                                        Text(hint, color = MaterialTheme.colorScheme.inversePrimary)
                                    },
                                    singleLine = true,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                        focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                        focusedTextColor = MaterialTheme.colorScheme.primary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.inversePrimary
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(
                                        onSearch = {
                                            focusManager.clearFocus()
                                            onSearch(query)
                                        }
                                    )
                                )
                            }
                        }
                    }

                    Crossfade(
                        targetState = query.isNotEmpty(),
                        animationSpec = tween(300), label = "search-clear-toggle"
                    ) { isQueryNotEmpty ->
                        if (isQueryNotEmpty) {
                            IconButton(onClick = {
                                query = "" // Clear
                                onSearch("") // Clear
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(dp40)
                                    .clickable {
                                        onMicClicked()
                                        if(PermissionHelper.isPermissionPending(
                                            context = context, permissionName = RECORD_AUDIO_PERMISSION
                                        ).not()) {
                                            isMicOpened = true
                                            voiceRecognizer.startListening()
                                        } else {
                                            PermissionHelper.requestPermission(
                                                context = context,
                                                permissionName = RECORD_AUDIO_PERMISSION,
                                                permissionRequestLauncher = micPermissionRequestLauncher
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                MicRipple(
                                    isSpeaking = isMicOpened,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Mic",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceRecognizer.stop()
        }
    }
}
