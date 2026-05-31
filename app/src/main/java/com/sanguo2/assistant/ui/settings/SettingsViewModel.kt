package com.sanguo2.assistant.ui.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.sanguo2.assistant.data.local.DataCacheManager
import com.sanguo2.assistant.data.repository.GameDataRepository
import com.sanguo2.assistant.data.repository.ImportResult
import com.sanguo2.assistant.util.BackupHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: GameDataRepository,
    private val application: Application
) : ViewModel() {
    private val _importResult = MutableStateFlow<ImportResult?>(null)
    val importResult: StateFlow<ImportResult?> = _importResult.asStateFlow()

    private val _isImporting = MutableStateFlow(false)
    val isImporting: StateFlow<Boolean> = _isImporting.asStateFlow()

    private val _backupResult = MutableStateFlow<String?>(null)
    val backupResult: StateFlow<String?> = _backupResult.asStateFlow()

    private val _lastUpdateTime = MutableStateFlow("")
    val lastUpdateTime: StateFlow<String> = _lastUpdateTime.asStateFlow()

    private val _dataVersion = MutableStateFlow("")
    val dataVersion: StateFlow<String> = _dataVersion.asStateFlow()

    private val _soldierCount = MutableStateFlow(0)
    val soldierCount: StateFlow<Int> = _soldierCount.asStateFlow()

    private val _formationCount = MutableStateFlow(0)
    val formationCount: StateFlow<Int> = _formationCount.asStateFlow()

    private val _rushRule = MutableStateFlow("")
    val rushRule: StateFlow<String> = _rushRule.asStateFlow()

    private val _speedNote = MutableStateFlow("")
    val speedNote: StateFlow<String> = _speedNote.asStateFlow()

    private val _readingNote = MutableStateFlow("")
    val readingNote: StateFlow<String> = _readingNote.asStateFlow()

    init {
        loadInfo()
    }

    private fun loadInfo() {
        val cacheManager = DataCacheManager(application)
        val time = cacheManager.getLastUpdateTime()
        _lastUpdateTime.value = if (time > 0) {
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                .format(java.util.Date(time))
        } else "内置数据"
        val data = repository.getCurrentData()
        _dataVersion.value = data?.version ?: "未知"
        _soldierCount.value = data?.soldiers?.size ?: 0
        _formationCount.value = data?.formations?.size ?: 0
        _rushRule.value = data?.rushRule ?: ""
        _speedNote.value = data?.speedNote ?: ""
        _readingNote.value = data?.readingNote ?: ""
    }

    fun importExcel(uri: Uri) {
        _isImporting.value = true
        try {
            val result = repository.importFromExcel(uri)
            _importResult.value = result
            if (result.success) {
                loadInfo()
            }
        } catch (e: Exception) {
            _importResult.value = ImportResult(false, "导入异常：${e.message}", null)
        } finally {
            _isImporting.value = false
        }
    }

    fun backupData() {
        val data = repository.getCurrentData()
        if (data == null) {
            _backupResult.value = "无数据可备份"
            return
        }
        val result = BackupHelper.backupToJson(application, data)
        _backupResult.value = if (result) "备份成功" else "备份失败"
    }

    fun restoreData(uri: Uri) {
        val result = BackupHelper.restoreFromJson(application, uri, repository)
        _backupResult.value = result
        if (result.startsWith("恢复成功")) {
            loadInfo()
        }
    }

    fun clearImportResult() {
        _importResult.value = null
    }

    fun clearBackupResult() {
        _backupResult.value = null
    }
}
