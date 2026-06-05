package com.sanguo2.assistant.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "soldier_configs",
    foreignKeys = [ForeignKey(
        entity = ForceNote::class,
        parentColumns = ["id"],
        childColumns = ["forceNoteId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SoldierConfig(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val forceNoteId: Long,
    val soldierType: String,
    val count: Int,
    val plannedCounterType: String? = null
)
