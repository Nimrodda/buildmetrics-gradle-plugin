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

package com.nimroddayan.buildmetrics.cache

import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class CacheManager(
    private val eventDao: EventDao
) {
    fun pushCachedEvents(client: Client, listeners: Set<BuildMetricsListener>) {
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
