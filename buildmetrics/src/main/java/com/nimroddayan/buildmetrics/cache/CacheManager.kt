package com.nimroddayan.buildmetrics.cache

import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class CacheManager(
    private val client: Client,
    private val eventDao: EventDao,
    private val listeners: List<BuildMetricsListener>
) {
    fun pushCachedEvents() {
        val cachedEvents = try {
            eventDao.selectAll()
        } catch (e: Exception) {
            log.debug(e) { "Error while getting cached events" }
            return
        }

        if (cachedEvents.isNotEmpty()) {
            log.info { "Pushing cached events" }
            cachedEvents.forEach { event ->
                listeners.forEach { listener ->
                    log.debug { "Pushing cache events to $listener" }
                    try {
                        listener.onBuildFinished(client, event)
                    } catch (e: Exception) {
                        log.debug(e) { "$listener onBuildFinished failed, aborting..." }
                        return
                    }
                }
            }
            log.info { "Done pushing cached events. Purging..." }
            eventDao.purge()
        }
    }
}
