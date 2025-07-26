package com.unique.schedify.auth.login.presentation

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.Identity
import com.unique.schedify.R
import com.unique.schedify.auth.login.presentation.login_common.LoginInputField
import com.unique.schedify.core.presentation.common_composables.DashedDivider
import com.unique.schedify.core.presentation.common_composables.GradientButton
import com.unique.schedify.core.presentation.common_composables.GradientCircularButton
import com.unique.schedify.core.presentation.common_composables.LoadingUi
import com.unique.schedify.core.presentation.navigation.Navigation
import com.unique.schedify.core.presentation.utils.isEmailValid
import com.unique.schedify.core.presentation.utils.size_units.dp12
import com.unique.schedify.core.presentation.utils.size_units.dp16
import com.unique.schedify.core.presentation.utils.size_units.dp8
import com.unique.schedify.core.presentation.utils.size_units.sp20
import com.unique.schedify.core.presentation.utils.size_units.sp32
import com.unique.schedify.core.presentation.utils.ui_utils.AppBaseGradients
import com.unique.schedify.core.presentation.utils.ui_utils.AvailableScreens
import com.unique.schedify.core.util.Resource

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
            .fillMaxSize()
            .background(
                brush = AppBaseGradients.baseBgGradient()
            ),
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
                    .wrapContentHeight()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(dp16),
                elevation = CardDefaults.cardElevation(dp8),
                colors = CardDefaults.cardColors(containerColor =
                MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Box(
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(dp16),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = dp8, vertical = dp16),
                            verticalArrangement = Arrangement.SpaceAround,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(R.string.guest),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = sp32,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            )
                            Spacer(modifier = Modifier.height(dp8))
                            Text(
                                text = stringResource(R.string.let_s_get_you),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.W400,
                                    fontSize = sp20,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            )
                        }

                        CreateLoginUI(
                            viewModel = viewModel
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dp16, vertical = dp12),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DashedDivider(
                                modifier = Modifier
                                    .weight(0.5f),
                                dividerColor = MaterialTheme.colorScheme.inversePrimary
                            )
                            Text(
                                text = " OR ",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    MaterialTheme.colorScheme.inversePrimary
                                )
                            )
                            DashedDivider(
                                modifier = Modifier
                                    .weight(0.5f),
                                dividerColor = MaterialTheme.colorScheme.inversePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(dp8))

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

        when(val state = viewModel.getOtpState.value) {
            is Resource.Default -> {}
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
fun CreateLoginUI(
    viewModel: LoginViewmodel
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInputFieldUi(
            loginViewmodel = viewModel,
            onRequestOtp = {
                keyboardController?.hide()
                viewModel.getOtp()
            }
        )
    }
}

@Composable
fun EmailInputFieldUi(
    loginViewmodel: LoginViewmodel,
    onRequestOtp: () -> Unit
) {

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LoginInputField(
            modifier = Modifier.weight(1f),
            loginViewmodel = loginViewmodel,
            enabled = loginViewmodel.getOtpState.value !is Resource.Success,
            onEmailValidDoneCallback = {
                onRequestOtp()
            }
        )

        Spacer(modifier = Modifier.width(dp8))

        GradientCircularButton(
            enabled = loginViewmodel.email.value.isEmailValid(),
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

