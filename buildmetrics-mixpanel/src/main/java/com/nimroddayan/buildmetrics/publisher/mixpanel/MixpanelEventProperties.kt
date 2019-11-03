package com.nimroddayan.buildmetrics.publisher.mixpanel

import com.squareup.moshi.Json

abstract class MixpanelEventProperties(
    @Json(name = "distinct_id") val distinctId: String,
    @Json(name = "token") val token: String
)
