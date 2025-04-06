package com.unique.schedify.pre_auth.splash.presentation

import androidx.lifecycle.ViewModel
import com.unique.schedify.core.config.SharedPrefConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewmodel @Inject constructor(
    private val sharedPrefConfig: SharedPrefConfig
): ViewModel() {
    fun isUserLoggedIn(): Boolean = sharedPrefConfig.getAuthToken()?.isNotEmpty() == true
}