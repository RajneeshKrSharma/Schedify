package com.unique.schedify.auth.login.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unique.schedify.R
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpRequestDto
import com.unique.schedify.auth.login.presentation.login_common.LoginInputField
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.common_composables.OutlinedGradientButton
import com.unique.schedify.core.presentation.common_composables.SquareBoundaryProgressBar
import com.unique.schedify.core.presentation.common_composables.getEditTextColors
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp60
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp16
import com.unique.schedify.core.presentation.utils.size_units.sp28
import com.unique.schedify.core.presentation.utils.ui_utils.AppBaseGradients
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

    BackHandler {
        viewModel.resetToInitialState()
        navController.popBackStack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = AppBaseGradients.baseBgGradient()),
        contentAlignment = Alignment.Center

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dp16),
            verticalArrangement = Arrangement.Center
        ) {

            Card(
                modifier = Modifier
                    .padding(dp8)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(dp16),
                elevation = CardDefaults.cardElevation(dp8),
                colors = CardDefaults.cardColors(containerColor =
                MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                CreateOtpUI(
                    viewModel = viewModel,
                    navController = navController
                )
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
fun CreateOtpUI(
    viewModel: LoginViewmodel,
    navController: NavController
) {
    val context = LocalContext.current

    val showResendOtpBtn by rememberUpdatedState(viewModel.resentOtpBtnState.value)
    val loginViaOtpState = viewModel.loginViaOtpState.value

    if (loginViaOtpState is Resource.Success
        || viewModel.convertAccessTokenState.value is Resource.Success
    ) {
        Navigation.navigateAndClearBackStackScreen(
            navigateTo = AvailableScreens.PostAuth.PostAuthDownloadAndSaveScreen,
            navController = navController
        )
    }

    else if (loginViaOtpState is Resource.Error) {
        Toast.makeText(context, loginViaOtpState.message, Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
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

            LoginInputField(
                modifier = Modifier.fillMaxWidth(),
                loginViewmodel = viewModel,
                enabled = false,
            )

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

            LoginInputField(
                modifier = Modifier.fillMaxWidth(),
                loginViewmodel = viewModel,
                enabled = false,
            )

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
                        colors = getEditTextColors(),
                        modifier = Modifier
                            .width(dp60)
                            .height(dp60),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = sp28,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = if(index+1 == MaxOtpField) ImeAction.Done else ImeAction.None,
                            autoCorrectEnabled = true
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if(index+1 == MaxOtpField) {
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
                        ),
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

        GradientButton(
            text = stringResource(R.string.login_via_otp),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            enabled = otpFieldState.all { it.isNotEmpty() },
            modifier = Modifier.fillMaxWidth(),
            onClick = {
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
        )
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