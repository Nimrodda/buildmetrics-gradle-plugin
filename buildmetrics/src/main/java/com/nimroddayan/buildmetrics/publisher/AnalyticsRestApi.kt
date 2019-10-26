package com.nimroddayan.buildmetrics.publisher

import com.nimroddayan.buildmetrics.tracker.Event

interface AnalyticsRestApi {
    fun trackEvent(event: Event): Boolean
}
