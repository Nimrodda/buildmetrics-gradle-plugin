package com.nimroddayan.buildmetrics.cache

import com.nimroddayan.buildmetrics.tracker.Event

interface EventDao {
    fun insert(event: Event)
}
