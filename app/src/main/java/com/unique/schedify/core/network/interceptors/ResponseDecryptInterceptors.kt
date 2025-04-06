package com.unique.schedify.core.network.interceptors

import android.util.Log
import com.unique.schedify.BuildConfig
import com.unique.schedify.core.network.utility.DecryptionUtils
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONObject
import java.io.IOException

class ResponseDecryptInterceptors : Interceptor {

    companion object {
        private const val ENCRYPTION_KEY = BuildConfig.DECRYPT_KEY
        private const val CONTENT_TYPE_JSON = "application/json"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val originalBody = originalResponse.body

        // Check if body is JSON
        if (originalBody?.contentType()?.toString()?.contains(CONTENT_TYPE_JSON) == true) {
            val bodyString = originalBody.string()
            val jsonObject = JSONObject(bodyString)
            Log.v("OkHttp", "<<<---- Response Body Before Decryption: $bodyString")


            try {
                // Extract encrypted data and IV
                val encryptedData = jsonObject.getString("encrypted_data")
                val iv = jsonObject.getString("iv")

                // Decrypt the data

                val decryptedData = DecryptionUtils.decryptData(encryptedData, iv, ENCRYPTION_KEY)
                Log.v("OkHttp", "<<<---- Response Body After Decryption: $decryptedData")

                // Log the request and response
                logRequest(chain)
                return logResponse(
                    originalResponse.newBuilder()
                        .body(decryptedData.toResponseBody(CONTENT_TYPE_JSON.toMediaTypeOrNull()))
                        .build()
                )

            } catch (e: Exception) {
                throw IOException("Decryption failed: ${e.message}", e)
            }
        }

        // Log request and response if not JSON or decryption is not required
        logRequest(chain)
        return logResponse(originalResponse)
    }

    private fun logRequest(chain: Interceptor.Chain) {
        with(chain.request()) {
            Log.d("OkHttp", " --> Request Method: $method")
            Log.d("OkHttp", " --> Request URL: $url")
            Log.d("OkHttp", " --> Request Headers: $headers")
            body?.let {
                val buffer = Buffer().apply { it.writeTo(this) }
                Log.v("OkHttp", " --> Request Body: ${buffer.readUtf8()}")
            }
        }
    }

    private fun logResponse(response: Response): Response {
        with(response) {
            Log.i("OkHttp", " <-- Response: $code $message")
            Log.i("OkHttp", " <-- Headers: $headers")
            body?.let {
                val content = it.string()
                Log.i("OkHttp", " <-- Response Body: $content")
                return newBuilder()
                    .body(content.toResponseBody(CONTENT_TYPE_JSON.toMediaTypeOrNull()))
                    .build()
            }
        }
        return response
    }
}
