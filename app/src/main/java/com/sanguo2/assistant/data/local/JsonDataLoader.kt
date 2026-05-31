package com.sanguo2.assistant.data.local

import android.content.Context
import com.google.gson.Gson
import com.sanguo2.assistant.data.model.GameData
import java.io.InputStreamReader

class JsonDataLoader(private val context: Context) {
    private val gson = Gson()

    fun loadFromAssets(): GameData? {
        return try {
            val inputStream = context.assets.open("game_data.json")
            val reader = InputStreamReader(inputStream, "UTF-8")
            gson.fromJson(reader, GameData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun loadFromFile(filePath: String): GameData? {
        return try {
            val file = java.io.File(filePath)
            if (!file.exists()) return null
            val reader = InputStreamReader(file.inputStream(), "UTF-8")
            gson.fromJson(reader, GameData::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
