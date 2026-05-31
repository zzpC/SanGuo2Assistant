package com.sanguo2.assistant.data.model

data class GameData(
    val version: String,
    val rushRule: String,
    val speedNote: String,
    val readingNote: String,
    val soldiers: List<Soldier>,
    val formations: List<Formation>
)
