package com.nimroddayan.buildmetrics.tracker

import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class EventProcessor(
    private val isOffline: Boolean,
    private val eventDao: EventDao,
    private val client: Client,
    private val listeners: List<BuildMetricsListener>
) {
    fun processEvent(event: BuildFinishedEvent) {
        log.debug { "Processing event: $event" }
        try {
            if (isOffline || listeners.isEmpty()) {
                log.debug { "User is offline or no listeners registered, caching analytics in local database" }
                eventDao.insert(event)
            } else {
                listeners.forEach { it.onBuildFinished(client, event) }
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
