package com.example.passwordmanager.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class CryptoManager(
    private val secureRandom: SecureRandom = SecureRandom(),
) {
    fun generateSalt(size: Int = 16): ByteArray = ByteArray(size).also(secureRandom::nextBytes)

    fun deriveKey(
        password: String,
        salt: ByteArray,
        iterations: Int,
    ): SecretKey {
        val keySpec = PBEKeySpec(password.toCharArray(), salt, iterations, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val encoded = factory.generateSecret(keySpec).encoded
        return SecretKeySpec(encoded, "AES")
    }

    fun createVerifier(secretKey: SecretKey): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(secretKey.encoded)
        return Base64.getEncoder().encodeToString(digest)
    }

    fun encrypt(plainText: String, secretKey: SecretKey): String {
        val nonce = ByteArray(12).also(secureRandom::nextBytes)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, nonce))
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(nonce + encrypted)
    }

    fun decrypt(cipherText: String, secretKey: SecretKey): String {
        val decoded = Base64.getDecoder().decode(cipherText)
        val nonce = decoded.copyOfRange(0, 12)
        val payload = decoded.copyOfRange(12, decoded.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, nonce))
        return cipher.doFinal(payload).toString(Charsets.UTF_8)
    }

    fun sha1(input: String): String {
        return MessageDigest.getInstance("SHA-1")
            .digest(input.toByteArray(Charsets.UTF_8))
            .joinToString(separator = "") { byte -> "%02X".format(byte) }
    }
}
