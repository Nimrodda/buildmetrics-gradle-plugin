package com.nimroddayan.buildmetrics.publisher

data class BuildFinishedEvent(
    val isSuccess: Boolean,
    val durationSeconds: Long,
    val freeRam: Long,
    val timestamp: Long = System.currentTimeMillis()
)
