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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.Identity
import com.unique.schedify.R
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp20
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.util.Resource
import com.unique.schedify.core.util.isEmailValid

@Composable
fun LoginScreen(
    viewModel: LoginViewmodel = hiltViewModel(),
    navController: NavController,
    context: Context
) {
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
        when(val state = viewModel.getOtpState.value) {
            is Resource.Default -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dp16)
                ) {
                    Column(
                        modifier = Modifier.weight(0.6f)
                    ) {
                        CreateLoginUI(
                            viewModel = viewModel
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
            }
            is Resource.Error -> {
                val errorMsg = state.message ?: "Error in get otp"
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
            is Resource.Loading -> {
                LoadingUi()
            }
            is Resource.Success -> {
                Navigation.navigateToScreen(
                    navigateTo = AvailableScreens.PreAuth.OtpInputScreen,
                    navController = navController,
                )
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

@Composable
fun CreateLoginUI(
    viewModel: LoginViewmodel
) {
    val keyboardController = LocalSoftwareKeyboardController.current

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

        EmailInputFieldUi(
            loginViewmodel = viewModel,
            onRequestOtp = {
                keyboardController?.hide()
                viewModel.getOtp(emailId = viewModel.email.value)
            }
        )
    }
}

@Composable
fun EmailInputFieldUi(
    loginViewmodel: LoginViewmodel,
    onRequestOtp: () -> Unit
) {
    val emailIdState = loginViewmodel.email

    Column {
        OutlinedTextField(
            value = emailIdState.value,
            onValueChange = { newValue ->
                emailIdState.value = newValue
            },
            label = { Text(stringResource(R.string.email_id)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth(),
            enabled = loginViewmodel.getOtpState.value !is Resource.Success,
            textStyle = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = sp20
            ),
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

        LoginButton(
            btnName = stringResource(R.string.request_otp),
            isEnabled = loginViewmodel.email.value.isEmailValid()
        ) {
            onRequestOtp()
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

