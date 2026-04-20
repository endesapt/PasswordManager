package com.example.passwordmanager.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CryptoManagerTest {
    private val cryptoManager = CryptoManager()

    @Test
    fun `encrypt and decrypt returns original value`() {
        val key = cryptoManager.deriveKey(
            password = "super-secret-password",
            salt = cryptoManager.generateSalt(),
            iterations = 1_000,
        )

        val encrypted = cryptoManager.encrypt("hello", key)
        val decrypted = cryptoManager.decrypt(encrypted, key)

        assertNotEquals("hello", encrypted)
        assertEquals("hello", decrypted)
    }

    @Test
    fun `same password and salt derive same verifier`() {
        val salt = cryptoManager.generateSalt()
        val first = cryptoManager.deriveKey("pw-12345678", salt, 2_000)
        val second = cryptoManager.deriveKey("pw-12345678", salt, 2_000)

        assertEquals(
            cryptoManager.createVerifier(first),
            cryptoManager.createVerifier(second),
        )
    }
}
