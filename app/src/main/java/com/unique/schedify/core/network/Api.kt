package com.unique.schedify.core.network

object Api {
    const val BASE_URL = "https://taskbreezeapi.pythonanywhere.com"
    const val PRE_APP_DETAILS = "/api/pre/app-details"

    // Login APIS
    const val GET_OTP = "/api/login/get-otp"
    const val LOGIN_VIA_OTP = "/api/login/login-via-otp"
    const val OAUTH_ACCESS_TOKEN = "https://oauth2.googleapis.com/token"
    const val CONVERT_ACCESS_TOKEN = "/auth/convert-token"

    //SPLIT EXPENSE APIS
    const val GROUP_EXPENSE = "api/list/group-expense"
    const val COLLABORATOR = "api/list/collaborator"

}