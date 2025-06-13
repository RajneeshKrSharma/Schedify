package com.unique.schedify.auth.login.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpRequestDto
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.common_composables.OutlinedGradientButton
import com.unique.schedify.core.presentation.common_composables.SquareBoundaryProgressBar
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp1
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp20
import com.unique.schedify.core.presentation.utils.size_units.dp60
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp16
import com.unique.schedify.core.presentation.utils.size_units.sp28
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.util.MaxOtpField
import com.unique.schedify.core.util.OtpExpiryInfo
import com.unique.schedify.core.util.Resource
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

    val showResendOtpBtn by rememberUpdatedState(viewModel.resentOtpBtnState.value)

    if (viewModel.loginViaOtpState.value is Resource.Success
        || viewModel.convertAccessTokenState.value is Resource.Success
    ) {
        Navigation.navigateAndClearBackStackScreen(
            navigateTo = AvailableScreens.PostAuth.PostAuthConsentScreen,
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
        if(showResendOtpBtn) {
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(R.string.request_otp),
                style = MaterialTheme.typography.headlineMedium,
            )

            EmailInfo(emailId = viewModel.email.value)

            GradientButton(
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    MaterialTheme.colorScheme.onSecondaryContainer
                ),
                text = stringResource(R.string.resend)
            ) {
                viewModel.getOtp()
                viewModel.showResendOtp(false)
            }

            Spacer(modifier = Modifier.height(dp16))

            OutlinedGradientButton(
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    MaterialTheme.colorScheme.primary
                ),
                text = stringResource(R.string.try_with_another_emailid),
                borderGradient = Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.primary,
                    )
                )
            ) {
                Navigation.navigateAndClearBackStackScreen(
                    navigateTo = AvailableScreens.PreAuth.LoginScreen,
                    navController = navController
                )
            }

        } else {

            Text(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(R.string.enter_otp),
                style = MaterialTheme.typography.headlineMedium,
            )

            EmailInfo(emailId = viewModel.email.value)

            OtpInputFieldsUi(
                viewModel = viewModel,
                onTimerFinished = {
                    Toast.makeText(context,
                        context.getString(R.string.otp_expired), Toast.LENGTH_SHORT).show()
                    viewModel.showResendOtp(true)
                }
            )
        }
    }
}

@Composable
fun EmailInfo(
    emailId : String
) {
    Spacer(modifier = Modifier.height(dp16))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dp8))
            .background(
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                shape = RoundedCornerShape(dp8)
            )
            .border(
                BorderStroke(dp1, MaterialTheme.colorScheme.inversePrimary),
                shape = RoundedCornerShape(dp8)
            ),
    ) {
        Text(
            modifier = Modifier
                .padding(dp20),
            text = emailId,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.inversePrimary
            ),
        )
    }

    Spacer(modifier = Modifier.height(dp16))
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
                        textStyle = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = sp28
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
                    LoginViaOtpRequestDto.default().copy(
                        emailId = viewModel.email.value,
                        otp = otpFieldState.joinToString(""),
                    )
                )
            }
        }
    }
}

@Composable
private fun OtpTimerUi(
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
        fontSize = sp16, color = MaterialTheme.colorScheme.secondary
    )
}