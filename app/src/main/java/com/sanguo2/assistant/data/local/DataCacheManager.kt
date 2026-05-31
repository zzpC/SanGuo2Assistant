package com.sanguo2.assistant.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.sanguo2.assistant.data.model.GameData

class DataCacheManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("game_data_cache", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val KEY_CACHED_DATA = "cached_game_data"
    private val KEY_DATA_VERSION = "data_version"
    private val KEY_LAST_UPDATE = "last_update_time"

    fun getCachedData(): GameData? {
        val json = prefs.getString(KEY_CACHED_DATA, null) ?: return null
        return try {
            gson.fromJson(json, GameData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun cacheData(data: GameData) {
        prefs.edit()
            .putString(KEY_CACHED_DATA, gson.toJson(data))
            .putString(KEY_DATA_VERSION, data.version)
            .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
            .apply()
    }

    fun getLastUpdateTime(): Long = prefs.getLong(KEY_LAST_UPDATE, 0)

    fun clearCache() {
        prefs.edit().clear().apply()
    }

    fun isCacheValid(): Boolean = getCachedData() != null
}
