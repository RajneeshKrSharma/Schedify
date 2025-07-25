package com.unique.schedify.core.network

object Api {
    //const val BASE_URL = "http://10.0.2.2:8000"
    const val BASE_URL = "https://schedify.pythonanywhere.com"
    const val PRE_APP_DETAILS = "/api/pre/app-details"

    // Login APIS
    const val GET_OTP = "/api/login/get-otp"
    const val LOGIN_VIA_OTP = "/api/login/login-via-otp"
    const val OAUTH_ACCESS_TOKEN = "https://oauth2.googleapis.com/token"
    const val CONVERT_ACCESS_TOKEN = "/auth/convert-token"

    //SPLIT EXPENSE APIS
    const val GROUP_EXPENSE = "api/list/groups"
    const val COLLABORATOR = "api/list/collaborators"
    const val EXPENSE = "api/list/expenses"

    //POST AUTH APIS
    const val POST_AUTH_DATA = "/api/post/getPostLoginDetails"

    //WEATHER USER MAPPED APIS
    const val USER_MAPPED_WEATHER_DATA = "/api/weather/user-mapped-forecast-data"

    const val SCHEDULE_LIST = "api/schedule-list/schedule-items"
    const val UPLOAD_ATTACHMENTS = "api/schedule-list/schedule-items/upload-attachments"
    const val PIN_CODE_VERIFY = "https://api.postalpincode.in/pincode/"
    const val ADD_ADDRESS = "/api/address/pincode"
    const val GET_STATUS = "/api/weather/get-status"
}