package com.example.passwordmanager.security

import android.util.Base64
import com.example.passwordmanager.data.local.dao.SecurityMetaDao
import com.example.passwordmanager.data.local.entity.SecurityMetaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.crypto.SecretKey

class DefaultMasterPasswordManager(
    private val securityMetaDao: SecurityMetaDao,
    private val cryptoManager: CryptoManager,
    private val iterations: Int = 120_000,
) : MasterPasswordManager {
    @Volatile
    private var currentKey: SecretKey? = null

    override suspend fun hasMasterPassword(): Boolean = withContext(Dispatchers.IO) {
        securityMetaDao.getMeta() != null
    }

    override suspend fun setupPassword(password: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            check(password.length >= 8) { "Master password must be at least 8 characters long." }
            check(securityMetaDao.getMeta() == null) { "Master password is already configured." }

            val salt = cryptoManager.generateSalt()
            val secretKey = cryptoManager.deriveKey(password, salt, iterations)
            val meta = SecurityMetaEntity(
                saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP),
                verifierBase64 = cryptoManager.createVerifier(secretKey),
                iterations = iterations,
                createdAt = System.currentTimeMillis(),
            )
            securityMetaDao.upsert(meta)
            currentKey = secretKey
        }
    }

    override suspend fun unlock(password: String): Boolean = withContext(Dispatchers.IO) {
        val meta = securityMetaDao.getMeta() ?: return@withContext false
        val salt = Base64.decode(meta.saltBase64, Base64.DEFAULT)
        val secretKey = cryptoManager.deriveKey(password, salt, meta.iterations)
        val verifier = cryptoManager.createVerifier(secretKey)
        val matches = verifier == meta.verifierBase64
        if (matches) {
            currentKey = secretKey
        }
        matches
    }

    override fun lock() {
        currentKey = null
    }

    override fun isUnlocked(): Boolean = currentKey != null

    override fun currentKeyOrNull(): SecretKey? = currentKey
}
