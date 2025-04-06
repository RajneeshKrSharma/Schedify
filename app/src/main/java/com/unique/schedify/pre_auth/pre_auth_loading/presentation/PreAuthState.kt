package com.unique.schedify.pre_auth.pre_auth_loading.presentation

data class PreAuthState(
    val isLoading: Boolean = false,
    val data: Float? = null,
    val error: String = ""
)