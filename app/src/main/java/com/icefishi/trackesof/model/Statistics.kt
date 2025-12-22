package com.icefishi.trackesof.model

data class Statistics(
    val todayCaught: Int = 0,
    val weekBest: Int = 0,
    val currentStreak: Int = 0,
    val totalCaught: Int = 0,
    val lastCatchDate: Long? = null
)

