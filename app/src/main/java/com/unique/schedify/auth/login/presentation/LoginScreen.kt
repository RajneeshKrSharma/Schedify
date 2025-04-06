package com.unique.schedify.auth.login.presentation

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.Identity
import com.unique.schedify.R
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpRequestDto
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.common_composables.SquareBoundaryProgressBar
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp60
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp16
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.util.EmailIdRegex
import com.unique.schedify.core.util.MaxOtpField
import com.unique.schedify.core.util.OtpExpiryInfo
import com.unique.schedify.core.util.Resource
import com.unique.schedify.core.util.isEmailValid
import com.unique.schedify.pre_auth.presentation.Screen
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: LoginViewmodel = hiltViewModel(),
    navController: NavController,
    context: Context
) {

    val emailIdState = remember { mutableStateOf("") }

    val authorizationLauncher = rememberAuthorizationLauncher(context = context)

    LaunchedEffect(viewModel.getAuthorizationRequest.value) {
        viewModel.getAuthorizationRequest.value?.let { request ->
            Identity.getAuthorizationClient(context)
                .authorize(request)
                .addOnSuccessListener { authorizationResult ->
                    if (authorizationResult.hasResolution()) {
                        authorizationResult.pendingIntent?.intentSender?.let { intentSender ->
                            authorizationLauncher.launch(
                                IntentSenderRequest.Builder(intentSender).build()
                            )
                        }
                    } else {
                        authorizationResult.serverAuthCode?.let { code ->
                            viewModel.getOAuthAccessToken(code = code)
                        } ?: Toast.makeText(
                            context,
                            context.getString(R.string.unable_to_fetch_authorization_details),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, e.stackTrace.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dp16)
        ) {
            Column(
                modifier = Modifier.weight(0.6f)
            ) {
                CreateLoginUI(
                    emailIdState = emailIdState,
                    viewModel = viewModel,
                    navController = navController
                )
            }

            Box(
                modifier = Modifier
                    .weight(0.4f),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    GradientButton(
                        text = stringResource(R.string.login_via_google),
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        btnGradient = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.onPrimaryContainer,
                                MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        ),
                        icon = R.drawable.google_icon,
                        iconModifier = Modifier.size(20.dp)
                    ) {
                        viewModel.onSignInWithGoogle(context = context)
                    }
                }
            }
        }

        with(viewModel) {
            if (getOtpState.value is Resource.Loading
                || loginViaOtpState.value is Resource.Loading
                || oAuthAccessTokenState.value is Resource.Loading
                || convertAccessTokenState.value is Resource.Loading
            ) {
                LoadingUi()
            }
        }
    }
}


@Composable
fun LoginButton(
    btnName: String,
    isEnabled: Boolean = false,
    onButtonPressed: () -> Unit
) {
    GradientButton(
        text = btnName,
        textStyle = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        enabled = isEnabled,
        modifier = Modifier.fillMaxWidth(),
        onClick = onButtonPressed
    )
}

val emailPattern = EmailIdRegex.toRegex()

@Composable
fun ObserveLoginErrors(viewModel: LoginViewmodel) {
    val context = LocalContext.current

    val errorMessage by remember {
        derivedStateOf {
            when {
                viewModel.getOtpState.value is Resource.Error -> viewModel.getOtpState.value.message
                viewModel.loginViaOtpState.value is Resource.Error -> viewModel.loginViaOtpState.value.message
                else -> null
            }
        }
    }

    val latestErrorMessage = rememberUpdatedState(errorMessage)

    LaunchedEffect(errorMessage) {
        latestErrorMessage.value?.let { msg ->
            if (msg.isNotEmpty()) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun CreateLoginUI(
    emailIdState: MutableState<String>,
    viewModel: LoginViewmodel,
    navController: NavController
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val timerState = remember { mutableIntStateOf(OtpExpiryInfo.OTP_EXPIRY_TIME.time) }
    val isTimerRunning = remember { mutableStateOf(false) }

    val context = LocalContext.current
    fun onTimerFinished() {
        Toast.makeText(context, "OTP Expired", Toast.LENGTH_SHORT).show()
        Navigation.navigateToScreen(
            navigateTo = AvailableScreens.PreAuth.LoginScreen,
            navController = navController,
            popUpToScreen = Screen.LoginScreen
        )
    }

    LaunchedEffect(viewModel.getOtpState.value) {
        if (viewModel.getOtpState.value is Resource.Success) {
            timerState.intValue = OtpExpiryInfo.OTP_EXPIRY_TIME.time / 1000
            isTimerRunning.value = true
            while (timerState.intValue > 0) {
                delay(1000L)
                timerState.intValue--
            }

            isTimerRunning.value = false
            onTimerFinished()
        }
    }

    ObserveLoginErrors(viewModel)

    if (viewModel.loginViaOtpState.value is Resource.Success
        || viewModel.convertAccessTokenState.value is Resource.Success
    ) {
        Navigation.navigateAndClearBackStackScreen(
            navigateTo = AvailableScreens.PostAuth.HomeScreen,
            navController = navController
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dp16),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.align(Alignment.Start),
            text = stringResource(R.string.let_s_login),
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Email Input Field
        OutlinedTextField(
            value = emailIdState.value,
            onValueChange = { newValue ->
                emailIdState.value = newValue
            },
            label = { Text(stringResource(R.string.email_id)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth(),
            enabled = viewModel.getOtpState.value !is Resource.Success,
            supportingText = {
                if (emailIdState.value.isNotEmpty() && emailIdState.value.isEmailValid().not()) {
                    Text(
                        stringResource(R.string.invalid_email_format),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(dp8))

        // ✅ Show OTP Input Section When OTP is Received
        if (viewModel.getOtpState.value is Resource.Success) {
            val otpFieldState = remember { mutableStateListOf("", "", "", "") }
            val focusManager = LocalFocusManager.current

            Text(
                modifier = Modifier.padding(bottom = dp16, top = dp8),
                text = stringResource(R.string.otp_will_expired_in_sec, timerState.intValue),
                fontSize = sp16, color = MaterialTheme.colorScheme.tertiary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(MaxOtpField) { index ->
                    Box(
                        modifier = Modifier
                            .width(dp60)
                            .height(dp60)
                    ) {
                        OutlinedTextField(
                            value = otpFieldState[index],
                            onValueChange = { newValue ->
                                if (newValue.length <= 1) {
                                    otpFieldState[index] = newValue
                                    if (newValue.isNotEmpty() && index < 3) {
                                        focusManager.moveFocus(FocusDirection.Right)
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(dp60)
                                .height(dp60),
                            textStyle = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        SquareBoundaryProgressBar(
                            modifier = Modifier
                                .width(dp60)
                                .height(dp60),
                            OtpExpiryInfo.OTP_EXPIRY_TIME.time
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dp16))

            // ✅ Login Button
            LoginButton(
                btnName = stringResource(R.string.login_via_otp),
                isEnabled = otpFieldState.all { it.isNotEmpty() }
            ) {
                if (otpFieldState.all { it.isNotEmpty() }) {
                    keyboardController?.hide()
                    viewModel.loginViaOtp(
                        LoginViaOtpRequestDto(
                            emailId = emailIdState.value,
                            otp = otpFieldState.joinToString(""),
                            fcmToken = "RANDOM"
                        )
                    )
                }
            }
        } else {
            // ✅ Request OTP Button
            LoginButton(
                btnName = stringResource(R.string.request_otp),
                isEnabled = emailIdState.value.isEmailValid()
            ) {
                keyboardController?.hide()
                viewModel.getOtp(emailId = emailIdState.value)
            }
        }
    }
}

@Composable
private fun rememberAuthorizationLauncher(
    context: Context
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
) { activityResult ->
    if (activityResult.resultCode == Activity.RESULT_OK) {
        Identity.getAuthorizationClient(context)
            .getAuthorizationResultFromIntent(activityResult.data)
    }
}

