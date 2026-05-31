package com.sanguo2.assistant.ui.soldier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanguo2.assistant.data.model.SoldierQueryResult
import com.sanguo2.assistant.data.repository.GameDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoldierQueryViewModel @Inject constructor(
    private val repository: GameDataRepository
) : ViewModel() {
    private val _queryResult = MutableStateFlow<SoldierQueryResult?>(null)
    val queryResult: StateFlow<SoldierQueryResult?> = _queryResult.asStateFlow()

    private val _soldierNames = MutableStateFlow<List<String>>(emptyList())
    val soldierNames: StateFlow<List<String>> = _soldierNames.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            repository.loadData()
            _soldierNames.value = repository.getAllSoldierNames()
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun querySoldier(enemyName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.queryCounterSoldiers(enemyName)
                if (result != null) {
                    _queryResult.value = result
                } else {
                    _errorMessage.value = "未找到兵种：$enemyName"
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
        _searchQuery.value = ""
    }

    fun getFilteredNames(): List<String> {
        val query = _searchQuery.value
        return if (query.isBlank()) _soldierNames.value
        else _soldierNames.value.filter { it.contains(query) }
    }
}
