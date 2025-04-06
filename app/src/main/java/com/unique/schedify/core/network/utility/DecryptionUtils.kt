package com.unique.schedify.core.network.utility

import android.util.Base64
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DecryptionUtils {

    // Decrypt the data using AES-CBC
    fun decryptData(encryptedData: String, iv: String, key: String): String {
        return try {
            // Ensure the key is 32 bytes (AES-256)
            val finalKey = key.padEnd(32, '0').substring(0, 32).toByteArray(Charsets.UTF_8)

            // Decode the encrypted data and IV from Base64
            val encryptedBytes = Base64.decode(encryptedData.trim(), Base64.DEFAULT)
            val ivBytes = Base64.decode(iv.trim(), Base64.DEFAULT)

            // Create and initialize the cipher
            Cipher.getInstance("AES/CBC/PKCS5Padding").apply {
                init(
                    Cipher.DECRYPT_MODE,
                    SecretKeySpec(finalKey, "AES"),
                    IvParameterSpec(ivBytes)
                )
            }.run {
                // Decrypt the data
                doFinal(encryptedBytes)
            }.let { decryptedBytes ->
                // Convert the decrypted bytes to a UTF-8 string
                String(decryptedBytes, StandardCharsets.UTF_8)
            }
        } catch (e: Exception) {
            throw IOException("Decryption failed: ${e.message}", e)
        }
    }
}