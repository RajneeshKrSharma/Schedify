package com.unique.schedify.core.network.converter_factory

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.unique.schedify.BuildConfig
import com.unique.schedify.core.network.utility.EncryptionUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class EncryptedRequestConverterFactory(
    private val gson: Gson
) : Converter.Factory() {

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter: TypeAdapter<*> = gson.getAdapter(TypeToken.get(type))
        return EncryptedRequestBodyConverter(adapter, gson)
    }

    private class EncryptedRequestBodyConverter<T : Any>(
        private val adapter: TypeAdapter<T>,
        private val gson: Gson
    ) : Converter<T, RequestBody> {

        override fun convert(value: T): RequestBody {
            return adapter.toJson(value).run { // Serialize the object to JSON
                EncryptionUtils.encryptData(this, BuildConfig.DECRYPT_KEY) // Encrypt the JSON string
            }.let { (encryptedData, iv) ->
                // Create the encrypted request body map
                mapOf(
                    "encrypted_data" to encryptedData,
                    "iv" to iv
                )
            }.run {
                // Convert the encrypted map to JSON
                gson.toJson(this)
            }.toRequestBody("application/json".toMediaTypeOrNull()) // Return the encrypted JSON as a request body
        }
    }
}