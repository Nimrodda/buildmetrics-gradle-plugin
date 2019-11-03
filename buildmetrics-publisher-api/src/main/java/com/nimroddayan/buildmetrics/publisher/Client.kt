package com.nimroddayan.buildmetrics.publisher

data class Client(
    val id: String,
    val osName: String,
    val osVersion: String,
    val cpu: String,
    val ram: String,
    val model: String
)
