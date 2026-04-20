package com.example.passwordmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.passwordmanager.data.local.entity.SecurityMetaEntity

@Dao
interface SecurityMetaDao {
    @Query("SELECT * FROM security_meta WHERE id = 0")
    suspend fun getMeta(): SecurityMetaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meta: SecurityMetaEntity)
}
