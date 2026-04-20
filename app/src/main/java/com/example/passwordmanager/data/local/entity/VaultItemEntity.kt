package com.example.passwordmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_items")
data class VaultItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val username: String,
    val encryptedPassword: String,
    val website: String,
    val notes: String,
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
