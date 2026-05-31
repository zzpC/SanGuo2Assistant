package com.sanguo2.assistant.data.repository

import android.content.Context
import android.net.Uri
import com.sanguo2.assistant.data.local.DataCacheManager
import com.sanguo2.assistant.data.local.ExcelParser
import com.sanguo2.assistant.data.local.JsonDataLoader
import com.sanguo2.assistant.data.model.*

class GameDataRepository(private val context: Context) {
    private val jsonLoader = JsonDataLoader(context)
    private val cacheManager = DataCacheManager(context)
    private val excelParser = ExcelParser()
    private var currentData: GameData? = null

    suspend fun loadData(): GameData? {
        currentData?.let { return it }
        cacheManager.getCachedData()?.let {
            currentData = it
            return it
        }
        val data = jsonLoader.loadFromAssets()
        if (data != null) {
            cacheManager.cacheData(data)
            currentData = data
        }
        return data
    }

    fun getCurrentData(): GameData? = currentData

    fun getSoldierByName(name: String): Soldier? {
        return currentData?.soldiers?.find { it.name == name }
    }

    fun getFormationByName(name: String): Formation? {
        return currentData?.formations?.find { it.name == name }
    }

    fun queryCounterSoldiers(enemyName: String): SoldierQueryResult? {
        val enemy = getSoldierByName(enemyName) ?: return null
        val data = currentData ?: return null
        val counterSoldiers = data.soldiers.mapNotNull { soldier ->
            val restraintValue = soldier.restraint[enemyName] ?: 0
            if (restraintValue > 0) {
                CounterSoldier(soldier, restraintValue, soldier.rangeType)
            } else null
        }.sortedByDescending { it.restraintValue }
        val rushInfo = buildRushInfo(enemy, data)
        return SoldierQueryResult(enemy, counterSoldiers, rushInfo)
    }

    private fun buildRushInfo(enemy: Soldier, data: GameData): String {
        val enemyRange = enemy.rangeValue
        val rushTargets = data.soldiers.filter { s ->
            when {
                enemyRange == 1 -> s.rangeValue in listOf(2, 3)
                enemyRange == 2 -> s.rangeValue == 3
                else -> false
            }
        }
        return if (rushTargets.isNotEmpty()) {
            "该兵种（${enemy.rangeType}）遇到以下兵种会全突：${rushTargets.joinToString("、") { it.name }}"
        } else {
            "该兵种（${enemy.rangeType}）不会触发全突"
        }
    }

    fun queryCounterFormations(enemyName: String): FormationRecommendResult? {
        val enemy = getFormationByName(enemyName) ?: return null
        val data = currentData ?: return null
        val recommendations = data.formations.mapNotNull { formation ->
            val restraintValue = formation.restraint[enemyName] ?: 0
            if (restraintValue > 0) {
                val reason = buildFormationReason(formation.name, enemyName, restraintValue)
                FormationRecommendation(formation, restraintValue, reason)
            } else null
        }.sortedByDescending { it.restraintValue }
        val description = buildFormationDescription(enemy, recommendations)
        return FormationRecommendResult(enemy, recommendations, description)
    }

    private fun buildFormationReason(formationName: String, enemyName: String, value: Int): String {
        return "${formationName}阵克制${enemyName}阵${value}点"
    }

    private fun buildFormationDescription(enemy: Formation, recommendations: List<FormationRecommendation>): String {
        if (recommendations.isEmpty()) {
            return "没有特别克制${enemy.name}阵的阵型"
        }
        val best = recommendations.first()
        return "推荐使用${best.formation.name}阵对抗${enemy.name}阵，克制值${best.restraintValue}点。" +
            recommendations.take(3).joinToString("；") {
                "${it.formation.name}阵(+${it.restraintValue})"
            }
    }

    fun importFromExcel(uri: Uri): ImportResult {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return ImportResult(false, "无法打开文件", null)
            val validation = excelParser.validateFormat(inputStream)
            if (!validation.isValid) {
                inputStream.close()
                return ImportResult(false, validation.message, null)
            }
            inputStream.close()
            val inputStream2 = context.contentResolver.openInputStream(uri)
                ?: return ImportResult(false, "无法打开文件", null)
            val data = excelParser.parse(inputStream2)
            inputStream2.close()
            if (data == null) {
                return ImportResult(false, "文件解析失败，数据结构异常", null)
            }
            if (data.soldiers.isEmpty()) {
                return ImportResult(false, "文件中未找到兵种数据", null)
            }
            cacheManager.cacheData(data)
            currentData = data
            ImportResult(true, "数据导入成功，共${data.soldiers.size}个兵种，${data.formations.size}个阵型", data)
        } catch (e: Exception) {
            ImportResult(false, "导入失败：${e.message}", null)
        }
    }

    fun getAllSoldierNames(): List<String> = currentData?.soldiers?.map { it.name } ?: emptyList()

    fun getAllFormationNames(): List<String> = currentData?.formations?.map { it.name } ?: emptyList()

    fun getSoldiersByRangeType(rangeType: String): List<Soldier> {
        return currentData?.soldiers?.filter { it.rangeType == rangeType } ?: emptyList()
    }
}

data class ImportResult(val success: Boolean, val message: String, val data: GameData?)
