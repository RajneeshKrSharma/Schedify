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
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_IS_OTP_LOGIN = "is_otp_login"
        private const val AUTH_USER_ID = "auth_user_id"
        private const val AUTH_USER_EMAIL = "auth_user_emailId"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_PIN_CODE = "pinCode"

        private const val KEY_CURRENT_TIME_STAMP = "current_time_stamp"
    }

    fun saveAuthToken(token: String) {
        sharedPreferences.edit { putString(KEY_AUTH_TOKEN, token) }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearAll() {
        sharedPreferences.edit { clear() }
    }

    fun isUserViaOtp(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_OTP_LOGIN, false)
    }

    fun saveIsUserViaOtp(isOtpLogin: Boolean) {
        sharedPreferences.edit { putBoolean(KEY_IS_OTP_LOGIN,  isOtpLogin) }
    }

    fun saveCurrentTimeStamp() {
        sharedPreferences.edit() { putLong(KEY_CURRENT_TIME_STAMP, System.currentTimeMillis()) }
    }

    fun getCurrentTimeStamp(): Long {
        return sharedPreferences.getLong(KEY_CURRENT_TIME_STAMP, 0L)
    }

    fun saveAuthUserId(token: Int) {
        sharedPreferences.edit { putInt(AUTH_USER_ID, token) }
    }

    fun getAuthUserId(): Int {
        return sharedPreferences.getInt(AUTH_USER_ID, -1)
    }

    fun saveAuthUserEmailId(email: String) {
        sharedPreferences.edit { putString(AUTH_USER_EMAIL, email) }
    }

    fun getAuthUserEmailId(): String? {
        return sharedPreferences.getString(AUTH_USER_EMAIL, null)
    }

    fun saveFcmToken(token: String) {
        sharedPreferences.edit { putString(KEY_FCM_TOKEN, token) }
    }

    fun getFcmToken(): String? {
        return sharedPreferences.getString(KEY_FCM_TOKEN, null)
    }

    fun savePinCode(pinCode: String) {
        return sharedPreferences.edit { putString(KEY_PIN_CODE, pinCode) }
    }

    fun getPinCode(): String? {
        return sharedPreferences.getString(KEY_PIN_CODE, null)
    }
}
