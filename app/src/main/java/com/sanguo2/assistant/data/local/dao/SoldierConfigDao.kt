package com.sanguo2.assistant.data.local.dao

import androidx.room.*
import com.sanguo2.assistant.data.local.entity.SoldierConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface SoldierConfigDao {
    @Query("SELECT * FROM soldier_configs WHERE forceNoteId = :forceNoteId")
    fun getConfigsForNote(forceNoteId: Long): Flow<List<SoldierConfig>>

    @Query("SELECT * FROM soldier_configs WHERE forceNoteId = :forceNoteId")
    suspend fun getConfigsForNoteSync(forceNoteId: Long): List<SoldierConfig>

    @Query("SELECT * FROM soldier_configs")
    fun getConfigsForAllNotes(): Flow<List<SoldierConfig>>

    @Insert
    suspend fun insert(config: SoldierConfig): Long

    @Insert
    suspend fun insertAll(configs: List<SoldierConfig>)

    @Update
    suspend fun update(config: SoldierConfig)

    @Delete
    suspend fun delete(config: SoldierConfig)

    @Query("DELETE FROM soldier_configs WHERE forceNoteId = :forceNoteId")
    suspend fun deleteByForceNoteId(forceNoteId: Long)
}
