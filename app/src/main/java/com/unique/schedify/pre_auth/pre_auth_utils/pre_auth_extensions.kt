package com.unique.schedify.pre_auth.pre_auth_utils

import com.google.gson.Gson
import com.unique.schedify.pre_auth.pre_auth_loading.data.remote.dto.GradientColors

fun String.toGradientColors(): GradientColors? {
    return try {
        val fixedJson = this.replace("'", "\"")
        Gson().fromJson(fixedJson, GradientColors::class.java)
    } catch (e: Exception) {
        null // Handle malformed string gracefully
    }
}