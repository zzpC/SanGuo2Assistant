package com.sanguo2.assistant.ui.formation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanguo2.assistant.data.model.FormationRecommendResult
import com.sanguo2.assistant.data.repository.GameDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormationRecommendViewModel @Inject constructor(
    private val repository: GameDataRepository
) : ViewModel() {
    private val _queryResult = MutableStateFlow<FormationRecommendResult?>(null)
    val queryResult: StateFlow<FormationRecommendResult?> = _queryResult.asStateFlow()

    private val _formationNames = MutableStateFlow<List<String>>(emptyList())
    val formationNames: StateFlow<List<String>> = _formationNames.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedFormation = MutableStateFlow("")
    val selectedFormation: StateFlow<String> = _selectedFormation.asStateFlow()

    init {
        viewModelScope.launch {
            repository.loadData()
            _formationNames.value = repository.getAllFormationNames()
        }
    }

    fun onFormationSelected(name: String) {
        _selectedFormation.value = name
    }

    fun queryFormation(enemyName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.queryCounterFormations(enemyName)
                if (result != null) {
                    _queryResult.value = result
                } else {
                    _errorMessage.value = "未找到阵型：$enemyName"
                }
            } catch (e: Exception) {
                _errorMessage.value = "查询失败：${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearResult() {
        _queryResult.value = null
        _selectedFormation.value = ""
    }
}
