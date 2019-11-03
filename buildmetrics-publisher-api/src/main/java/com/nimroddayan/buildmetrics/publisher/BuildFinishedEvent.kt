package com.nimroddayan.buildmetrics.publisher

data class BuildFinishedEvent(
    val isSuccess: Boolean,
    val durationSeconds: Long,
    val freeRam: String,
    val swapRam: String,
    val timestamp: Long = System.currentTimeMillis()
)
