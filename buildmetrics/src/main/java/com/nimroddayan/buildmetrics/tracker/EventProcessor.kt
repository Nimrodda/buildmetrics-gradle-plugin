package com.nimroddayan.buildmetrics.tracker

import com.nimroddayan.buildmetrics.Event
import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class EventProcessor(
    private val isOffline: Boolean,
    private val eventDao: EventDao,
    private val clientUid: String,
    private val trackingId: String,
    private val analyticsRestApi: AnalyticsRestApi
) {
    fun processEvent(event: Event) {
        log.debug { "Processing event: $event" }
        try {
            if (isOffline || !analyticsRestApi.trackEvent(trackingId, clientUid, event)) {
                log.debug { "User in offline or request failed, caching analytics in local database" }
                eventDao.insert(event)
            }
        } catch (e: Exception) {
            log.debug(e) { "Failed to track event, attempting to store event in local database" }
            try {
                eventDao.insert(event)
            } catch (e: Exception) {
                log.debug(e) { "Failed to store event to local database, ignoring..."}
            }
        }
    }
}
