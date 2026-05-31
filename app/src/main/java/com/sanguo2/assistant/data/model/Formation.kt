package com.sanguo2.assistant.data.model

data class Formation(
    val id: Int,
    val name: String,
    val restraint: Map<String, Int>,
    val counters: List<CounterInfo>,
    val counteredBy: List<CounterInfo>
)
