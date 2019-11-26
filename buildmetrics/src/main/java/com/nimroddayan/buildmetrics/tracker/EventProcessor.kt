/*
 *    Copyright 2019 Nimrod Dayan nimroddayan.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.nimroddayan.buildmetrics.tracker

import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

internal class EventProcessor(
    private val isOffline: Boolean,
    private val eventDao: EventDao,
    private val client: Client,
    private val listeners: Set<BuildMetricsListener>
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
