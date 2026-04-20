package com.example.passwordmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_meta")
data class SecurityMetaEntity(
    @PrimaryKey val id: Int = 0,
    val saltBase64: String,
    val verifierBase64: String,
    val iterations: Int,
    val createdAt: Long,
)
