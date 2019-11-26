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

import com.nimroddayan.buildmetrics.EventQueries
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent

internal interface EventDao {
    fun insert(event: BuildFinishedEvent)
    fun selectAll(): List<BuildFinishedEvent>
    fun delete(timestamp: Long)
    fun purge()
}

internal class EventDaoSqlite(
    private val eventsQueries: EventQueries
) : EventDao {
    override fun insert(event: BuildFinishedEvent) {
        eventsQueries.insert(
            timestamp = System.currentTimeMillis(),
            is_success = event.isSuccess,
            duration_seconds = event.durationSeconds,
            free_ram = event.freeRam,
            swap_ram = event.swapRam,
            task_names = event.taskNames
        )
    }

    override fun selectAll(): List<BuildFinishedEvent> {
        return eventsQueries.selectAll { timestamp, isSuccess, durationSeconds, freeRam, swapRam, taskNames ->
            BuildFinishedEvent(
                timestamp = timestamp,
                isSuccess = isSuccess,
                durationSeconds = durationSeconds,
                freeRam = freeRam,
                swapRam = swapRam,
                taskNames = taskNames
            )
        }.executeAsList()
    }

    override fun delete(timestamp: Long) {
        eventsQueries.delete(timestamp)
    }

    override fun purge() {
        eventsQueries.purge()
    }
}

internal class EventDaoNoOp : EventDao {
    override fun insert(event: BuildFinishedEvent) {
    }

    override fun selectAll(): List<BuildFinishedEvent> {
        return emptyList()
    }

    override fun delete(timestamp: Long) {
    }

    override fun purge() {
    }
}
