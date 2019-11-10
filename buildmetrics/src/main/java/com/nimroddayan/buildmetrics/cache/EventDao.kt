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
            free_ram = event.freeRam,
            swap_ram = event.swapRam
        )
    }

    override fun selectAll(): List<BuildFinishedEvent> {
        return eventsQueries.selectAll { timestamp, isSuccess, durationSeconds, freeRam, swapRam ->
            BuildFinishedEvent(
                timestamp = timestamp,
                isSuccess = isSuccess,
                durationSeconds = durationSeconds,
                freeRam = freeRam,
                swapRam = swapRam
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

class EventDaoNoOp : EventDao {
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
