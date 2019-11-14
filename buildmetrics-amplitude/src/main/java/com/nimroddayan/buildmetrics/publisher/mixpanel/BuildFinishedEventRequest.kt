package com.nimroddayan.buildmetrics.publisher.mixpanel

import com.squareup.moshi.Json

data class BuildFinishedEventRequest(
    @Json(name = "api_key") val apiKey: String,
    @Json(name = "events") val events: List<BuildFinishedEventType>
)

data class BuildFinishedEventType(
    @Json(name = "user_id") val userId: String,
    @Json(name = "event_type") val eventType: String,
    @Json(name = "time") val time: Long,
    @Json(name = "event_properties") val eventProperties: BuildFinishedEventProperties,
    @Json(name = "user_properties") val userProperties: UserProperties,
    @Json(name = "os_name") val osName: String,
    @Json(name = "os_version") val osVersion: String,
    @Json(name = "device_model") val model: String
)

class BuildFinishedEventProperties(
    @Json(name = "build_success") val isBuildSuccess: Boolean,
    @Json(name = "build_duration") val buildDuration: Long,
    @Json(name = "build_free_ram") val buildFreeRam: String,
    @Json(name = "build_swap_ram") val buildSwapRam: String,
    @Json(name = "build_task_names") val buildTaskNames: String
)

data class UserProperties(
    @Json(name = "cpu") val cpu: String,
    @Json(name = "ram") val ram: String
)
