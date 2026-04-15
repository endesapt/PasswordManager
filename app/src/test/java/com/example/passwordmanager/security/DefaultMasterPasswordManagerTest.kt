package com.example.passwordmanager.security

import com.example.passwordmanager.data.local.dao.SecurityMetaDao
import com.example.passwordmanager.data.local.entity.SecurityMetaEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultMasterPasswordManagerTest {
    private val dao = FakeSecurityMetaDao()
    private val manager = DefaultMasterPasswordManager(
        securityMetaDao = dao,
        cryptoManager = CryptoManager(),
        iterations = 500,
    )

    @Test
    fun `setup stores metadata and unlock succeeds`() = runTest {
        val result = manager.setupPassword("my-master-password")

        assertTrue(result.isSuccess)
        assertTrue(manager.hasMasterPassword())
        manager.lock()
        assertTrue(manager.unlock("my-master-password"))
        assertTrue(manager.isUnlocked())
    }

    @Test
    fun `unlock fails for wrong password`() = runTest {
        manager.setupPassword("my-master-password")
        manager.lock()

        assertFalse(manager.unlock("wrong-password"))
    }
}

private class FakeSecurityMetaDao : SecurityMetaDao {
    private var meta: SecurityMetaEntity? = null

    override suspend fun getMeta(): SecurityMetaEntity? = meta

    override suspend fun upsert(meta: SecurityMetaEntity) {
        this.meta = meta
    }
}
