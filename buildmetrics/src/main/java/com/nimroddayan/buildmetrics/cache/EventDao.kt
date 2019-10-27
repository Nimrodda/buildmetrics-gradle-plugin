package com.nimroddayan.buildmetrics.cache

import com.nimroddayan.buildmetrics.EventQueries
import com.nimroddayan.buildmetrics.Event

interface EventDao {
    fun insert(event: Event)
    fun selectAll(): List<Event>
    fun delete(id: Long)
    fun purge()
}

class EventDaoSqlite(
    private val eventsQueries: EventQueries
) : EventDao {
    override fun insert(event: Event) {
        eventsQueries.insert(
            category = event.category,
            action = event.action,
            label = event.label,
            value = event.value
        )
    }

    override fun selectAll(): List<Event> {
        return eventsQueries.selectAll().executeAsList()
    }

    override fun delete(id: Long) {
        eventsQueries.delete(id)
    }

    override fun purge() {
        eventsQueries.purge()
    }
}
