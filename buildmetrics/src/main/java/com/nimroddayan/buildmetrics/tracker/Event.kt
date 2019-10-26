package com.nimroddayan.buildmetrics.tracker

data class Event(
    val trackingId: String,
    val uid: String,
    val category: String,
    val action: String,
    val label: String,
    val value: String
)
