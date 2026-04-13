package com.example.passwordmanager.domain.repository

import com.example.passwordmanager.domain.model.VaultItem
import kotlinx.coroutines.flow.Flow

interface VaultRepository {
    fun observeVaultItems(): Flow<List<VaultItem>>
    suspend fun getVaultItem(id: Long): VaultItem?
    suspend fun upsertVaultItem(item: VaultItem): Long
    suspend fun deleteVaultItem(id: Long)
}
