package com.example.passwordmanager.data.repository

import com.example.passwordmanager.data.local.dao.VaultItemDao
import com.example.passwordmanager.data.local.entity.VaultItemEntity
import com.example.passwordmanager.domain.model.VaultItem
import com.example.passwordmanager.security.CryptoManager
import com.example.passwordmanager.security.MasterPasswordManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import javax.crypto.SecretKey

class DefaultVaultRepositoryTest {
    private val cryptoManager = CryptoManager()
    private val key = cryptoManager.deriveKey(
        password = "vault-pass",
        salt = ByteArray(16) { 1 },
        iterations = 400,
    )
    private val dao = FakeVaultItemDao()
    private val manager = FakeMasterPasswordManager(key)
    private val repository = DefaultVaultRepository(dao, manager, cryptoManager)

    @Test
    fun `upsert encrypts password and returns decrypted item`() = runTest {
        val id = repository.upsertVaultItem(
            VaultItem(
                title = "GitHub",
                username = "octocat",
                password = "PlainPassword123",
                website = "github.com",
            ),
        )

        val storedEntity = dao.getById(id)!!
        assertNotEquals("PlainPassword123", storedEntity.encryptedPassword)

        val loaded = repository.getVaultItem(id)!!
        assertEquals("PlainPassword123", loaded.password)
        assertEquals("GitHub", loaded.title)
    }

    @Test
    fun `observeVaultItems emits decrypted values`() = runTest {
        repository.upsertVaultItem(
            VaultItem(title = "Email", username = "me", password = "mail-pass"),
        )

        val items = repository.observeVaultItems().first()

        assertEquals(1, items.size)
        assertEquals("mail-pass", items.first().password)
    }
}

private class FakeVaultItemDao : VaultItemDao {
    private val items = mutableListOf<VaultItemEntity>()
    private val flow = MutableStateFlow<List<VaultItemEntity>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<VaultItemEntity>> = flow

    override suspend fun getById(id: Long): VaultItemEntity? = items.firstOrNull { it.id == id }

    override suspend fun upsert(item: VaultItemEntity): Long {
        val entity = if (item.id == 0L) {
            item.copy(id = nextId++)
        } else {
            item
        }
        items.removeAll { it.id == entity.id }
        items.add(entity)
        flow.value = items.sortedByDescending { it.updatedAt }
        return entity.id
    }

    override suspend fun deleteById(id: Long) {
        items.removeAll { it.id == id }
        flow.value = items.toList()
    }
}

private class FakeMasterPasswordManager(
    private val key: SecretKey,
) : MasterPasswordManager {
    private var unlocked = true

    override suspend fun hasMasterPassword(): Boolean = true

    override suspend fun setupPassword(password: String): Result<Unit> = Result.success(Unit)

    override suspend fun unlock(password: String): Boolean {
        unlocked = true
        return true
    }

    override fun lock() {
        unlocked = false
    }

    override fun isUnlocked(): Boolean = unlocked

    override fun currentKeyOrNull(): SecretKey? = key.takeIf { unlocked }
}
