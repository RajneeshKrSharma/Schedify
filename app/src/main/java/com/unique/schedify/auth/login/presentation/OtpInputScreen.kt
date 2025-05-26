package com.unique.schedify.auth.login.presentation

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpRequestDto
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.common_composables.SquareBoundaryProgressBar
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp60
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp16
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.util.MaxOtpField
import com.unique.schedify.core.util.OtpExpiryInfo
import com.unique.schedify.core.util.Resource
import com.unique.schedify.pre_auth.presentation.Screen
import kotlinx.coroutines.delay

@Composable
fun OtpInputScreen(
    viewModel: LoginViewmodel = hiltViewModel(),
    navController: NavController,
) {
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
                CreateOtpUI(
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }

        with(viewModel) {
            if (getOtpState.value is Resource.Loading
                || oAuthAccessTokenState.value is Resource.Loading
                || convertAccessTokenState.value is Resource.Loading
            ) {
                LoadingUi()
            }
        }
    }
}

@Composable
fun CreateOtpUI(
    viewModel: LoginViewmodel,
    navController: NavController
) {
    val context = LocalContext.current

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
            text = "Enter Otp",
            style = MaterialTheme.typography.headlineLarge,
        )

        Spacer(modifier = Modifier.height(dp8))

        OtpInputFieldsUi(
            viewModel = viewModel,
            onTimerFinished = {
                Toast.makeText(context, "OTP Expired", Toast.LENGTH_SHORT).show()
                Navigation.navigateAndClearBackStackScreen(
                    navigateTo = AvailableScreens.PreAuth.LoginScreen,
                    navController = navController
                )
            }
        )
    }
}

@Composable
fun OtpInputFieldsUi(
    viewModel: LoginViewmodel,
    onTimerFinished: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    // ✅ Show OTP Input Section When OTP is Received
    if (viewModel.getOtpState.value is Resource.Success) {
        val otpFieldState = remember { mutableStateListOf("", "", "", "") }
        val focusManager = LocalFocusManager.current

        OtpTimerUi(
            loginViewmodel = viewModel,
            onTimerFinished = onTimerFinished
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
                        emailId = viewModel.email.value,
                        otp = otpFieldState.joinToString(""),
                        fcmToken = "RANDOM"
                    )
                )
            }
        }
    }
}

@Composable
private fun OtpTimerUi(
    loginViewmodel: LoginViewmodel,
    onTimerFinished: () -> Unit,
) {
    val timerState = remember { mutableIntStateOf(OtpExpiryInfo.OTP_EXPIRY_TIME.time) }
    val isTimerRunning = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        timerState.intValue = OtpExpiryInfo.OTP_EXPIRY_TIME.time / 1000
        isTimerRunning.value = true
        while (timerState.intValue > 0) {
            delay(1000L)
            timerState.intValue--
        }

        isTimerRunning.value = false
        onTimerFinished()
    }

    Text(
        modifier = Modifier.padding(bottom = dp16, top = dp8),
        text = stringResource(R.string.otp_will_expired_in_sec, timerState.intValue),
        fontSize = sp16, color = MaterialTheme.colorScheme.tertiary
    )
}