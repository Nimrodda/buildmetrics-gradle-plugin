package com.nimroddayan.buildmetrics.publisher.mixpanel

import com.squareup.moshi.Json

data class BuildFinishedEventRequest(
    @Json(name = "event") val event: String,
    @Json(name = "properties") val properties: BuildFinishedEventProperties
)

class BuildFinishedEventProperties(
    @Json(name = "build_success") val isBuildSuccess: Boolean,
    @Json(name = "build_duration") val buildDuration: Long,
    @Json(name = "build_free_ram") val buildFreeRam: String,
    @Json(name = "build_swap_ram") val buildSwapRam: String,
    @Json(name = "build_task_names") val buildTaskNames: String,
    distinctId: String,
    token: String
) : MixpanelEventProperties(distinctId, token)
