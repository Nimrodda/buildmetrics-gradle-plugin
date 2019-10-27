package com.nimroddayan.buildmetrics.publisher

import com.nimroddayan.buildmetrics.Event

interface AnalyticsRestApi {
    fun trackEvent(trackingId: String, uid: String, event: Event): Boolean
}
