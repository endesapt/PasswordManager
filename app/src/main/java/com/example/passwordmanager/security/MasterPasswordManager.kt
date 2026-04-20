package com.example.passwordmanager.security

import javax.crypto.SecretKey

interface MasterPasswordManager {
    suspend fun hasMasterPassword(): Boolean
    suspend fun setupPassword(password: String): Result<Unit>
    suspend fun unlock(password: String): Boolean
    fun lock()
    fun isUnlocked(): Boolean
    fun currentKeyOrNull(): SecretKey?
}
