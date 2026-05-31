package com.sanguo2.assistant.data.local.dao

import androidx.room.*
import com.sanguo2.assistant.data.local.entity.ForceNote
import kotlinx.coroutines.flow.Flow

@Dao
interface ForceNoteDao {
    @Query("SELECT * FROM force_notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<ForceNote>>

    @Query("SELECT * FROM force_notes WHERE id = :id")
    suspend fun getNoteById(id: Long): ForceNote?

    @Query("SELECT * FROM force_notes WHERE forceType = 'city' AND cityName LIKE '%' || :cityName || '%' ORDER BY updatedAt DESC")
    fun getNotesByCityName(cityName: String): Flow<List<ForceNote>>

    @Query("SELECT * FROM force_notes WHERE forceType = 'field' AND customLabel LIKE '%' || :label || '%' ORDER BY updatedAt DESC")
    fun getNotesByCustomLabel(label: String): Flow<List<ForceNote>>

    @Query("SELECT DISTINCT cityName FROM force_notes WHERE forceType = 'city' AND cityName != '' ORDER BY cityName")
    fun getAllCityNames(): Flow<List<String>>

    @Insert
    suspend fun insert(note: ForceNote): Long

    @Update
    suspend fun update(note: ForceNote)

    @Delete
    suspend fun delete(note: ForceNote)

    @Query("DELETE FROM force_notes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM force_notes")
    suspend fun deleteAll()
}
