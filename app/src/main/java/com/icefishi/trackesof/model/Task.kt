package com.icefishi.trackesof.model

import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val size: TaskSize = TaskSize.MEDIUM,
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaskSize(val displayName: String, val sizeMultiplier: Float) {
    SMALL("Small", 0.5f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.8f)
}

