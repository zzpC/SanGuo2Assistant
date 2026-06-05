package com.sanguo2.assistant.ui.garrison

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanguo2.assistant.data.local.entity.ForceNote
import com.sanguo2.assistant.data.local.entity.SoldierConfig
import com.sanguo2.assistant.data.repository.ForceNoteWithConfigs
import com.sanguo2.assistant.data.repository.GameDataRepository
import com.sanguo2.assistant.data.repository.GarrisonNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForceNoteUiState(
    val notes: List<ForceNoteWithConfigs> = emptyList(),
    val cityNames: List<String> = emptyList(),
    val soldierNames: List<String> = emptyList(),
    val filterCityName: String = "",
    val filterLabel: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class NoteDetailUiState(
    val noteWithConfigs: ForceNoteWithConfigs? = null,
    val isLoading: Boolean = false
)

data class SoldierConfigUiItem(
    val id: Long = 0,
    val soldierType: String = "",
    val plannedCounterType: String = ""
)

data class NoteEditUiState(
    val id: Long = 0,
    val forceType: String = "city",
    val cityName: String = "",
    val customLabel: String = "",
    val remark: String = "",
    val soldierConfigs: List<SoldierConfigUiItem> = listOf(SoldierConfigUiItem()),
    val isEdit: Boolean = false,
    val cityNameError: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class ForceNoteViewModel @Inject constructor(
    private val repository: GarrisonNoteRepository,
    private val gameDataRepository: GameDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForceNoteUiState())
    val uiState: StateFlow<ForceNoteUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow(NoteDetailUiState())
    val detailState: StateFlow<NoteDetailUiState> = _detailState.asStateFlow()

    private val _editState = MutableStateFlow(NoteEditUiState())
    val editState: StateFlow<NoteEditUiState> = _editState.asStateFlow()

    init {
        viewModelScope.launch {
            gameDataRepository.loadData()
            _uiState.update { it.copy(soldierNames = gameDataRepository.getAllSoldierNames()) }
        }
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                repository.getAllNotesWithConfigs(),
                repository.getAllCityNames()
            ) { notes, cities ->
                _uiState.update { it.copy(
                    notes = notes,
                    cityNames = cities,
                    isLoading = false
                )}
            }.collect()
        }
    }

    fun setFilterCityName(name: String) {
        _uiState.update { it.copy(filterCityName = name) }
        refreshFilteredNotes()
    }

    fun setFilterLabel(label: String) {
        _uiState.update { it.copy(filterLabel = label) }
        refreshFilteredNotes()
    }

    fun clearFilters() {
        _uiState.update { it.copy(filterCityName = "", filterLabel = "") }
        refreshFilteredNotes()
    }

    private fun refreshFilteredNotes() {
        val city = _uiState.value.filterCityName
        val label = _uiState.value.filterLabel
        viewModelScope.launch {
            val flow = when {
                city.isNotBlank() -> repository.getNotesByCityName(city)
                label.isNotBlank() -> repository.getNotesByCustomLabel(label)
                else -> repository.getAllNotesWithConfigs()
            }
            flow.collect { notes ->
                _uiState.update { it.copy(notes = notes) }
            }
        }
    }

    fun deleteNote(noteWithConfigs: ForceNoteWithConfigs) {
        viewModelScope.launch {
            repository.deleteNote(noteWithConfigs)
            _uiState.update { it.copy(successMessage = "已删除备注") }
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            repository.deleteAllNotes()
            _uiState.update { it.copy(successMessage = "已清空所有备注") }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    suspend fun getNoteById(id: Long): ForceNoteWithConfigs? {
        return repository.getNoteWithConfigs(id)
    }

    fun initEditState(noteWithConfigs: ForceNoteWithConfigs? = null) {
        if (noteWithConfigs != null) {
            val note = noteWithConfigs.note
            _editState.update { it.copy(
                id = note.id,
                forceType = note.forceType,
                cityName = note.cityName,
                customLabel = note.customLabel,
                remark = note.remark,
                soldierConfigs = noteWithConfigs.configs.flatMap { cfg ->
                    List(cfg.count) { 
                        SoldierConfigUiItem(
                            id = cfg.id, 
                            soldierType = cfg.soldierType,
                            plannedCounterType = cfg.plannedCounterType ?: ""
                        ) 
                    }
                },
                isEdit = true,
                cityNameError = null,
                isSaving = false,
                saveSuccess = false
            )}
        } else {
            _editState.update { it.copy(
                id = 0,
                forceType = "city",
                cityName = "",
                customLabel = "",
                remark = "",
                soldierConfigs = listOf(SoldierConfigUiItem()),
                isEdit = false,
                cityNameError = null,
                isSaving = false,
                saveSuccess = false
            )}
        }
    }

    fun onForceTypeChanged(type: String) {
        _editState.update { it.copy(forceType = type) }
    }

    fun onCityNameChanged(name: String) {
        _editState.update { it.copy(cityName = name, cityNameError = null) }
    }

    fun onCustomLabelChanged(label: String) {
        _editState.update { it.copy(customLabel = label) }
    }

    fun onRemarkChanged(remark: String) {
        _editState.update { it.copy(remark = remark) }
    }

    fun addSoldierConfig() {
        val configs = _editState.value.soldierConfigs.toMutableList()
        configs.add(SoldierConfigUiItem())
        _editState.update { it.copy(soldierConfigs = configs) }
    }

    fun removeSoldierConfig(index: Int) {
        val configs = _editState.value.soldierConfigs.toMutableList()
        if (configs.size > 1) {
            configs.removeAt(index)
            _editState.update { it.copy(soldierConfigs = configs) }
        }
    }

    fun onSoldierTypeChanged(index: Int, type: String) {
        val configs = _editState.value.soldierConfigs.toMutableList()
        if (index in configs.indices) {
            configs[index] = configs[index].copy(soldierType = type)
            _editState.update { it.copy(soldierConfigs = configs) }
        }
    }

    fun onPlannedCounterTypeChanged(index: Int, type: String) {
        val configs = _editState.value.soldierConfigs.toMutableList()
        if (index in configs.indices) {
            configs[index] = configs[index].copy(plannedCounterType = type)
            _editState.update { it.copy(soldierConfigs = configs) }
        }
    }

    fun saveNote() {
        val state = _editState.value
        var hasError = false

        val validConfigs = mutableListOf<SoldierConfigUiItem>()
        for (cfg in state.soldierConfigs) {
            if (cfg.soldierType.isBlank()) continue
            validConfigs.add(cfg)
        }

        if (validConfigs.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "至少需要一种兵种配置") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _editState.update { it.copy(isSaving = true) }
            try {
                val note = ForceNote(
                    id = if (state.isEdit) state.id else 0,
                    forceType = state.forceType,
                    cityName = if (state.forceType == "city") state.cityName.trim() else "",
                    customLabel = if (state.forceType == "field") state.customLabel.trim() else "",
                    remark = state.remark.trim(),
                    updatedAt = System.currentTimeMillis()
                )
                
                val configsToSave = validConfigs.map { item ->
                    SoldierConfig(
                        id = 0,
                        forceNoteId = note.id,
                        soldierType = item.soldierType,
                        count = 1,
                        plannedCounterType = item.plannedCounterType.ifBlank { null }
                    )
                }

                if (state.isEdit) {
                    val oldNoteWithConfigs = repository.getNoteWithConfigs(state.id)
                    if (oldNoteWithConfigs != null) {
                        repository.updateNote(oldNoteWithConfigs, note, configsToSave)
                    }
                } else {
                    repository.insertNote(note.copy(createdAt = System.currentTimeMillis()), configsToSave)
                }
                _editState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _editState.update { it.copy(isSaving = false) }
                _uiState.update { it.copy(errorMessage = "保存失败：${e.message}") }
            }
        }
    }

    fun loadNoteDetail(noteId: Long) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true) }
            val noteWithConfigs = repository.getNoteWithConfigs(noteId)
            _detailState.update { it.copy(noteWithConfigs = noteWithConfigs, isLoading = false) }
        }
    }

    fun updatePlannedCounterType(configId: Long, noteId: Long, plannedType: String) {
        viewModelScope.launch {
            repository.updatePlannedCounterType(configId, plannedType)
            loadNoteDetail(noteId)
        }
    }
}
