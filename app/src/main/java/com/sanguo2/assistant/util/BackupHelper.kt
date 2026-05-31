package com.sanguo2.assistant.util

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.sanguo2.assistant.data.local.DataCacheManager
import com.sanguo2.assistant.data.model.GameData
import com.sanguo2.assistant.data.repository.GameDataRepository
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object BackupHelper {
    private const val BACKUP_DIR = "backups"
    private const val BACKUP_FILE = "game_data_backup.json"
    private const val ENCRYPTION_KEY = "SanGuo2Assistant"
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"

    fun backupToJson(context: Context, data: GameData): Boolean {
        return try {
            val backupDir = File(context.filesDir, BACKUP_DIR)
            if (!backupDir.exists()) backupDir.mkdirs()
            val backupFile = File(backupDir, BACKUP_FILE)
            val json = Gson().toJson(data)
            val encrypted = encrypt(json)
            backupFile.writeText(encrypted)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun restoreFromJson(context: Context, uri: Uri, repository: GameDataRepository): String {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return "无法打开文件"
            val json = inputStream.bufferedReader().readText()
            inputStream.close()
            val decrypted = try { decrypt(json) } catch (_: Exception) { json }
            val data = Gson().fromJson(decrypted, GameData::class.java)
                ?: return "文件格式错误"
            if (data.soldiers.isEmpty()) return "文件中无有效数据"
            val cacheManager = DataCacheManager(context)
            cacheManager.cacheData(data)
            "恢复成功，共${data.soldiers.size}个兵种，${data.formations.size}个阵型"
        } catch (e: Exception) {
            "恢复失败：${e.message}"
        }
    }

    private fun encrypt(data: String): String {
        val key = SecretKeySpec(padKey(ENCRYPTION_KEY), ALGORITHM)
        val iv = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        val combined = iv + encrypted
        return android.util.Base64.encodeToString(combined, android.util.Base64.DEFAULT)
    }

    private fun decrypt(data: String): String {
        val combined = android.util.Base64.decode(data, android.util.Base64.DEFAULT)
        val iv = combined.copyOfRange(0, 16)
        val encrypted = combined.copyOfRange(16, combined.size)
        val key = SecretKeySpec(padKey(ENCRYPTION_KEY), ALGORITHM)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }

    private fun padKey(key: String): ByteArray {
        val bytes = key.toByteArray(Charsets.UTF_8)
        return ByteArray(16).also { System.arraycopy(bytes, 0, it, 0, minOf(bytes.size, 16)) }
    }
}
