package com.sanguo2.assistant.data.model

import com.google.gson.annotations.SerializedName

data class Soldier(
    val id: Int,
    val name: String,
    val description: String,
    val rangeType: String,
    val rangeValue: Int,
    val infantryRange: Int?,
    val cavalryRange: Int?,
    val infantryRangedSpeed: Int?,
    val infantryMeleeSpeed: Int?,
    val cavalryRangedSpeed: Int?,
    val cavalryMeleeSpeed: Int?,
    val totalRestraint: Int,
    val restraint: Map<String, Int>,
    val counters: List<CounterInfo>,
    val counteredBy: List<CounterInfo>
)

data class CounterInfo(
    val name: String,
    val value: Int
)
