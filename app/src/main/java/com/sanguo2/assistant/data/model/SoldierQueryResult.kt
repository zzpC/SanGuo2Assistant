package com.sanguo2.assistant.data.model

data class SoldierQueryResult(
    val enemySoldier: Soldier,
    val counterSoldiers: List<CounterSoldier>,
    val rushInfo: String
)

data class CounterSoldier(
    val soldier: Soldier,
    val restraintValue: Int,
    val rangeType: String
)

data class FormationRecommendResult(
    val enemyFormation: Formation,
    val recommendedFormations: List<FormationRecommendation>,
    val description: String
)

data class FormationRecommendation(
    val formation: Formation,
    val restraintValue: Int,
    val reason: String
)
