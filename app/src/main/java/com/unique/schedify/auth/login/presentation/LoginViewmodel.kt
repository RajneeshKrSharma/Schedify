package com.unique.schedify.auth.login.presentation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.unique.schedify.auth.login.data.remote.dto.ConvertAccessTokenRequestDto
import com.unique.schedify.auth.login.data.remote.dto.ConvertAccessTokenResponseDto
import com.unique.schedify.auth.login.data.remote.dto.GetOtpRequestDto
import com.unique.schedify.auth.login.data.remote.dto.GetOtpResponseDto
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpRequestDto
import com.unique.schedify.auth.login.data.remote.dto.LoginViaOtpResponseDto
import com.unique.schedify.auth.login.data.remote.dto.OAuthAccessTokenRequestDto
import com.unique.schedify.auth.login.data.remote.dto.OAuthAccessTokenResponseDto
import com.unique.schedify.auth.login.domain.use_case.ConvertAccessTokenUseCase
import com.unique.schedify.auth.login.domain.use_case.LoginViaOtpUseCase
import com.unique.schedify.auth.login.domain.use_case.OAuthAccessTokenUseCase
import com.unique.schedify.auth.login.domain.use_case.RequestOtpUseCase
import com.unique.schedify.core.config.SharedPrefConfig
import com.unique.schedify.core.util.ApiResponseResource
import com.unique.schedify.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewmodel @Inject constructor(
    private val requestOtpUseCase: RequestOtpUseCase,
    private val loginViaOtpUseCase: LoginViaOtpUseCase,
    private val oAuthAccessTokenUseCase: OAuthAccessTokenUseCase,
    private val convertAccessTokenUseCase: ConvertAccessTokenUseCase,
    private val sharedPrefConfig: SharedPrefConfig
) : ViewModel() {

    private val _getOtpState = mutableStateOf<Resource<GetOtpResponseDto>>(Resource.Default())
    val getOtpState: State<Resource<GetOtpResponseDto>> = _getOtpState

    private val _getAuthorizationRequest = mutableStateOf<AuthorizationRequest?>(null)
    val getAuthorizationRequest: State<AuthorizationRequest?> = _getAuthorizationRequest

    private val _loginViaOtpState = mutableStateOf<Resource<LoginViaOtpResponseDto>>(Resource.Default())
    val loginViaOtpState: State<Resource<LoginViaOtpResponseDto>> = _loginViaOtpState


    private val _oAuthAccessTokenState = mutableStateOf<Resource<OAuthAccessTokenResponseDto>>(Resource.Default())
    val oAuthAccessTokenState: State<Resource<OAuthAccessTokenResponseDto>> = _oAuthAccessTokenState

    private val _convertAccessTokenState = mutableStateOf<Resource<ConvertAccessTokenResponseDto>>(Resource.Default())
    val convertAccessTokenState: State<Resource<ConvertAccessTokenResponseDto>> = _convertAccessTokenState

    fun getOtp(emailId: String) {
        viewModelScope.launch {
            _getOtpState.value = Resource.Loading()
            with(requestOtpUseCase.execute(GetOtpRequestDto(emailId = emailId))) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        _getOtpState.value = Resource.Success(this.data)
                    }

                    is ApiResponseResource.Error -> {
                        _getOtpState.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

    fun loginViaOtp(loginRequest: LoginViaOtpRequestDto) {
        viewModelScope.launch {
            _loginViaOtpState.value = Resource.Loading()
            with(loginViaOtpUseCase.execute(loginRequest)) {
                when (this) {
                    is ApiResponseResource.Error -> _loginViaOtpState.value = Resource.Error(this.errorMessage)
                    is ApiResponseResource.Success -> {
                        sharedPrefConfig.saveAuthToken(data.data?.authData?.key ?: "")
                        sharedPrefConfig.saveIsUserViaOtp(true)
                        _loginViaOtpState.value = Resource.Success(this.data)
                    }
                }
            }
        }
    }

    fun onSignInWithGoogle(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val googleIdOption = GetSignInWithGoogleOption.Builder("482265056733-agegp92ae28rj328qbscq15md226asc1.apps.googleusercontent.com")
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val response = withContext(Dispatchers.IO) { // Ensure proper suspension
                    CredentialManager.create(context).getCredential(
                        request = request,
                        context = context
                    )
                }
                onCredentialManagerSignedIn(response)

            } catch (e: GetCredentialCancellationException) {
                Log.e("SchedifyExceptions", "User manually cancelled the sign-in process")
            } catch (e: GetCredentialException) {
                Log.e("SchedifyExceptions", "Credential error: ${e.message}")
            } catch (e: Exception) {
                Log.e("SchedifyExceptions", "Unknown error: ${e.message}")
            }
        }
    }

    private fun onCredentialManagerSignedIn(response: GetCredentialResponse) {
        val credential = response.credential

        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                // val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                // Our backend is not waiting for this value, it is waiting for a server auth code
                // For this reason, we need to make the authorization request to get the server auth code

                val requestedScopes = listOf(
                    Scope("https://www.googleapis.com/auth/userinfo.email"),
                    Scope("https://www.googleapis.com/auth/userinfo.profile"),
                )

                val authorizationRequest: AuthorizationRequest =
                    AuthorizationRequest.builder()
                        .setRequestedScopes(requestedScopes)
                        .requestOfflineAccess("482265056733-agegp92ae28rj328qbscq15md226asc1.apps.googleusercontent.com")
                        .build()

                _getAuthorizationRequest.value = authorizationRequest

            } catch (e: Exception) {
                Log.d("SchedifyExceptions", "onCredentialManagerSignedIn: ${e.stackTrace}")
            }
        }
    }

    fun getOAuthAccessToken(code: String) {
        viewModelScope.launch {
            _oAuthAccessTokenState.value = Resource.Loading()
            with(oAuthAccessTokenUseCase.execute(OAuthAccessTokenRequestDto.empty().copy(code = code))) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        this.data.accessToken?.let { accessToken ->
                            _oAuthAccessTokenState.value = Resource.Success(this.data)
                            getConvertAccessToken(accessToken)

                        } ?: {
                            _oAuthAccessTokenState.value = Resource.Error("null access token found")
                        }
                    }

                    is ApiResponseResource.Error -> {
                        _oAuthAccessTokenState.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }

    private fun getConvertAccessToken(oAuthAccessToken: String) {
        viewModelScope.launch {
            _convertAccessTokenState.value = Resource.Loading()
            with(convertAccessTokenUseCase.execute(ConvertAccessTokenRequestDto.empty().copy(token = oAuthAccessToken))) {
                when (this) {
                    is ApiResponseResource.Success -> {
                        this.data.accessToken?.let { token ->
                            sharedPrefConfig.saveAuthToken(token)
                            sharedPrefConfig.saveIsUserViaOtp(false)
                            _convertAccessTokenState.value = Resource.Success(this.data)

                        } ?: {
                            _oAuthAccessTokenState.value = Resource.Error("null access token found")
                        }
                    }

                    is ApiResponseResource.Error -> {
                        _convertAccessTokenState.value = Resource.Error(this.errorMessage)
                    }
                }
            }
        }
    }



}