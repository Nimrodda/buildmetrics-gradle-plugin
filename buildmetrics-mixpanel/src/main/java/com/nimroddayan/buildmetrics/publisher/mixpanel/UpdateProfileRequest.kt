package com.nimroddayan.buildmetrics.publisher.mixpanel

import com.squareup.moshi.Json

data class UpdateProfileRequest(
    @Json(name = "\$token") val token: String,
    @Json(name = "\$distinct_id") val distinctId: String,
    @Json(name = "\$set") val clientInfo: ClientInfo
)

data class ClientInfo(
    @Json(name = "os_name") val osName: String,
    @Json(name = "os_version") val osVersion: String,
    @Json(name = "cpu") val cpu: String,
    @Json(name = "ram") val ram: String,
    @Json(name = "model") val model: String
)
