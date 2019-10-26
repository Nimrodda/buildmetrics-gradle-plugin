package com.nimroddayan.buildmetrics

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import com.nimroddayan.buildmetrics.tracker.Event
import com.nimroddayan.buildmetrics.tracker.EventProcessor
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class EventProcessorTest {

    @Mock
    private lateinit var analyticsRestApi: AnalyticsRestApi

    @Mock
    private lateinit var eventDao: EventDao

    private val event = Event("", "", "", "", "", "")

    @Test
    fun `track build finished when online calls rest api`() {
        whenever(analyticsRestApi.trackEvent(any())).thenReturn(true)

        val eventProcessor = EventProcessor(
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao,
            isOffline = false
        )

        eventProcessor.processEvent(event)

        verifyZeroInteractions(eventDao)
    }

    @Test
    fun `track build finished when offline calls eventDao`() {
        val eventProcessor = EventProcessor(
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao,
            isOffline = true
        )

        eventProcessor.processEvent(event)

        verify(eventDao).insert(any())
        verifyZeroInteractions(analyticsRestApi)
    }

    @Test
    fun `track build finished call to rest api throws fallback to eventDao`() {
        whenever(analyticsRestApi.trackEvent(any())).thenThrow(RuntimeException::class.java)

        val eventProcessor = EventProcessor(
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao,
            isOffline = false
        )

        eventProcessor.processEvent(event)

        verify(eventDao).insert(any())
    }

    @Test
    fun `track build finished call to rest api fails fallback to eventDao`() {
        whenever(analyticsRestApi.trackEvent(any())).thenReturn(false)

        val eventProcessor = EventProcessor(
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao,
            isOffline = false
        )

        eventProcessor.processEvent(event)

        verify(eventDao).insert(any())
    }
}
