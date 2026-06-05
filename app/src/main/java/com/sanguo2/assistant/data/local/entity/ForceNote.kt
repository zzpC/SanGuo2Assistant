package com.sanguo2.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "force_notes")
data class ForceNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val forceType: String,
    val cityName: String,
    val customLabel: String,
    val remark: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
