package com.nimroddayan.buildmetrics.cache

import com.nimroddayan.buildmetrics.EventQueries
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent

interface EventDao {
    fun insert(event: BuildFinishedEvent)
    fun selectAll(): List<BuildFinishedEvent>
    fun delete(timestamp: Long)
    fun purge()
}

class EventDaoSqlite(
    private val eventsQueries: EventQueries
) : EventDao {
    override fun insert(event: BuildFinishedEvent) {
        eventsQueries.insert(
            timestamp = System.currentTimeMillis(),
            is_success = event.isSuccess,
            duration_seconds = event.durationSeconds,
            free_ram = event.freeRam
        )
    }

    override fun selectAll(): List<BuildFinishedEvent> {
        return eventsQueries.selectAll { timestamp, is_success, duration_seconds, free_ram ->
            BuildFinishedEvent(
                timestamp = timestamp,
                isSuccess = is_success,
                durationSeconds = duration_seconds,
                freeRam = free_ram
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
