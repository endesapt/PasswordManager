package com.example.passwordmanager.data.repository

import com.example.passwordmanager.data.local.dao.VaultItemDao
import com.example.passwordmanager.data.local.entity.VaultItemEntity
import com.example.passwordmanager.domain.model.VaultItem
import com.example.passwordmanager.domain.repository.VaultRepository
import com.example.passwordmanager.security.CryptoManager
import com.example.passwordmanager.security.MasterPasswordManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.crypto.SecretKey

class DefaultVaultRepository(
    private val vaultItemDao: VaultItemDao,
    private val masterPasswordManager: MasterPasswordManager,
    private val cryptoManager: CryptoManager,
) : VaultRepository {
    override fun observeVaultItems(): Flow<List<VaultItem>> {
        return vaultItemDao.observeAll().map { items ->
            val key = masterPasswordManager.currentKeyOrNull() ?: return@map emptyList()
            items.map { entity -> entity.toDomain(key) }
        }
    }

    override suspend fun getVaultItem(id: Long): VaultItem? = withContext(Dispatchers.IO) {
        val key = masterPasswordManager.currentKeyOrNull() ?: return@withContext null
        vaultItemDao.getById(id)?.toDomain(key)
    }

    override suspend fun upsertVaultItem(item: VaultItem): Long = withContext(Dispatchers.IO) {
        val key = checkNotNull(masterPasswordManager.currentKeyOrNull()) {
            "Vault is locked. Unlock it before saving items."
        }
        vaultItemDao.upsert(item.toEntity(key))
    }

    override suspend fun deleteVaultItem(id: Long) = withContext(Dispatchers.IO) {
        vaultItemDao.deleteById(id)
    }

    private fun VaultItemEntity.toDomain(key: SecretKey): VaultItem {
        return VaultItem(
            id = id,
            title = title,
            username = username,
            password = cryptoManager.decrypt(encryptedPassword, key),
            website = website,
            notes = notes,
            isFavorite = isFavorite,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    private fun VaultItem.toEntity(key: SecretKey): VaultItemEntity {
        val now = System.currentTimeMillis()
        return VaultItemEntity(
            id = id,
            title = title.trim(),
            username = username.trim(),
            encryptedPassword = cryptoManager.encrypt(password, key),
            website = website.trim(),
            notes = notes.trim(),
            isFavorite = isFavorite,
            createdAt = if (id == 0L) now else createdAt,
            updatedAt = now,
        )
    }
}
