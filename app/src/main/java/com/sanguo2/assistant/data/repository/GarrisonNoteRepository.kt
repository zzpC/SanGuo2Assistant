package com.sanguo2.assistant.data.repository

import com.sanguo2.assistant.data.local.dao.ForceNoteDao
import com.sanguo2.assistant.data.local.dao.SoldierConfigDao
import com.sanguo2.assistant.data.local.entity.ForceNote
import com.sanguo2.assistant.data.local.entity.SoldierConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

data class ForceNoteWithConfigs(
    val note: ForceNote,
    val configs: List<SoldierConfig>
)

@Singleton
class GarrisonNoteRepository @Inject constructor(
    private val forceNoteDao: ForceNoteDao,
    private val soldierConfigDao: SoldierConfigDao
) {
    fun getAllNotesWithConfigs(): Flow<List<ForceNoteWithConfigs>> {
        return forceNoteDao.getAllNotes().combine(soldierConfigDao.getConfigsForAllNotes()) { notes, configs ->
            notes.map { note ->
                ForceNoteWithConfigs(note, configs.filter { it.forceNoteId == note.id })
            }
        }
    }

    suspend fun getNoteWithConfigs(id: Long): ForceNoteWithConfigs? {
        val note = forceNoteDao.getNoteById(id) ?: return null
        val configs = soldierConfigDao.getConfigsForNoteSync(id)
        return ForceNoteWithConfigs(note, configs)
    }

    fun getNotesByCityName(cityName: String): Flow<List<ForceNoteWithConfigs>> {
        return forceNoteDao.getNotesByCityName(cityName).combine(soldierConfigDao.getConfigsForAllNotes()) { notes, configs ->
            notes.map { note -> ForceNoteWithConfigs(note, configs.filter { it.forceNoteId == note.id }) }
        }
    }

    fun getNotesByCustomLabel(label: String): Flow<List<ForceNoteWithConfigs>> {
        return forceNoteDao.getNotesByCustomLabel(label).combine(soldierConfigDao.getConfigsForAllNotes()) { notes, configs ->
            notes.map { note -> ForceNoteWithConfigs(note, configs.filter { it.forceNoteId == note.id }) }
        }
    }

    fun getAllCityNames(): Flow<List<String>> = forceNoteDao.getAllCityNames()

    suspend fun insertNote(note: ForceNote, configs: List<SoldierConfig>): Long {
        val id = forceNoteDao.insert(note)
        val configsWithId = configs.map { it.copy(forceNoteId = id) }
        soldierConfigDao.insertAll(configsWithId)
        return id
    }

    suspend fun updateNote(oldNote: ForceNoteWithConfigs, newNote: ForceNote, newConfigs: List<SoldierConfig>) {
        forceNoteDao.update(newNote)
        soldierConfigDao.deleteByForceNoteId(newNote.id)
        soldierConfigDao.insertAll(newConfigs.map { it.copy(forceNoteId = newNote.id) })
    }

    suspend fun deleteNote(noteWithConfigs: ForceNoteWithConfigs) {
        forceNoteDao.deleteById(noteWithConfigs.note.id)
    }

    suspend fun deleteAllNotes() {
        forceNoteDao.deleteAll()
    }
}
