package com.unique.schedify.core.network.utility

import android.util.Base64
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {

    fun encryptData(data: String, key: String): Pair<String, String> {
        return try {
            // Ensure the key is 32 bytes (AES-256)
            val finalKey = key.padEnd(32, '0').substring(0, 32).toByteArray(Charsets.UTF_8)

            // Generate a random IV (16 bytes for AES)
            val ivBytes = ByteArray(16).apply {
                SecureRandom().nextBytes(this)  // Initialize the IV with random bytes
            }

            // Create the cipher object for AES encryption
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding").apply {
                val ivSpec = IvParameterSpec(ivBytes)  // Initialize IVSpec for the cipher
                val secretKeySpec =
                    SecretKeySpec(finalKey, "AES")  // Initialize the secret key for the cipher
                init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)  // Set up cipher for encryption
            }

            // Return the encrypted data and IV as a Pair
            Pair(
                cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
                    .let { Base64.encodeToString(it, Base64.DEFAULT).trim() },
                ivBytes.let { Base64.encodeToString(it, Base64.DEFAULT).trim() })

        } catch (e: Exception) {
            throw IOException("Encryption failed: ${e.message}", e)
        }
    }
}