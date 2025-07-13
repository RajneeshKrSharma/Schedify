package com.unique.schedify.core.config

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SharedPrefConfig @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val USER_LEVEL_PREFIX = "user_"
        private const val APP_LEVEL_PREFIX = "app_"

        // App-level keys
        private const val APP_KEY_FCM_TOKEN = "${APP_LEVEL_PREFIX}fcm_token"
        private const val APP_KEY_PIN_CODE = "${APP_LEVEL_PREFIX}pin_code"

        // User-level keys
        private const val USER_KEY_AUTH_TOKEN = "${USER_LEVEL_PREFIX}auth_token"
        private const val USER_KEY_IS_OTP_LOGIN = "${USER_LEVEL_PREFIX}is_otp_login"
        private const val USER_KEY_AUTH_USER_ID = "${USER_LEVEL_PREFIX}auth_user_id"
        private const val USER_KEY_AUTH_USER_EMAIL = "${USER_LEVEL_PREFIX}auth_user_email"
    }

    // --------------------
    // App-level functions
    // --------------------
    fun saveFcmToken(token: String) {
        sharedPreferences.edit { putString(APP_KEY_FCM_TOKEN, token) }
    }

    fun getFcmToken(): String? {
        return sharedPreferences.getString(APP_KEY_FCM_TOKEN, null)
    }

    fun savePinCode(pinCode: String) {
        sharedPreferences.edit { putString(APP_KEY_PIN_CODE, pinCode) }
    }

    fun getPinCode(): String? {
        return sharedPreferences.getString(APP_KEY_PIN_CODE, null)
    }

    // --------------------
    // User-level functions
    // --------------------
    fun saveAuthToken(token: String) {
        sharedPreferences.edit { putString(USER_KEY_AUTH_TOKEN, token) }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(USER_KEY_AUTH_TOKEN, null)
    }

    fun saveIsUserViaOtp(isOtpLogin: Boolean) {
        sharedPreferences.edit { putBoolean(USER_KEY_IS_OTP_LOGIN, isOtpLogin) }
    }

    fun isUserViaOtp(): Boolean {
        return sharedPreferences.getBoolean(USER_KEY_IS_OTP_LOGIN, false)
    }

    fun saveAuthUserId(id: Int) {
        sharedPreferences.edit { putInt(USER_KEY_AUTH_USER_ID, id) }
    }

    fun getAuthUserId(): Int {
        return sharedPreferences.getInt(USER_KEY_AUTH_USER_ID, -1)
    }

    fun saveAuthUserEmailId(email: String) {
        sharedPreferences.edit { putString(USER_KEY_AUTH_USER_EMAIL, email) }
    }

    fun getAuthUserEmailId(): String? {
        return sharedPreferences.getString(USER_KEY_AUTH_USER_EMAIL, null)
    }

    // --------------------
    // Clear methods
    // --------------------
    fun clearUserData() {
        sharedPreferences.edit {
            sharedPreferences.all.keys
                .filter { it.startsWith("user_") } // or your chosen prefix
                .forEach { remove(it) }
        }
    }
}
