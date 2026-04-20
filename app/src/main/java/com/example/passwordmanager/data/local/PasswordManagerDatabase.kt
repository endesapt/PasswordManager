package com.example.passwordmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.passwordmanager.data.local.dao.SecurityMetaDao
import com.example.passwordmanager.data.local.dao.VaultItemDao
import com.example.passwordmanager.data.local.entity.SecurityMetaEntity
import com.example.passwordmanager.data.local.entity.VaultItemEntity

@Database(
    entities = [VaultItemEntity::class, SecurityMetaEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class PasswordManagerDatabase : RoomDatabase() {
    abstract fun vaultItemDao(): VaultItemDao
    abstract fun securityMetaDao(): SecurityMetaDao
}
