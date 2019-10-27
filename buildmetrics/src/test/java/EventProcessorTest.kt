package com.nimroddayan.buildmetrics

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
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

    private val event = Event.Impl(0, "", "", "", "")

    @Test
    fun `track build finished when online calls rest api`() {
        whenever(analyticsRestApi.trackEvent(any(), any(), any())).thenReturn(true)

        val eventProcessor = EventProcessor(
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao,
            isOffline = false,
            trackingId = "foo",
            clientUid = "uid"
        )

        eventProcessor.processEvent(event)

        verifyZeroInteractions(eventDao)
    }

    @Test
    fun `track build finished when offline calls eventDao`() {
        val eventProcessor = EventProcessor(
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao,
            isOffline = true,
            trackingId = "foo",
            clientUid = "uid"
        )

        eventProcessor.processEvent(event)

        verify(eventDao).insert(any())
        verifyZeroInteractions(analyticsRestApi)
    }

    @Test
    fun `track build finished call to rest api throws fallback to eventDao`() {
        whenever(analyticsRestApi.trackEvent(any(), any(), any())).thenThrow(RuntimeException::class.java)

        val eventProcessor = EventProcessor(
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao,
            isOffline = false,
            trackingId = "foo",
            clientUid = "uid"
        )

        eventProcessor.processEvent(event)

        verify(eventDao).insert(any())
    }

    @Test
    fun `track build finished call to rest api fails fallback to eventDao`() {
        whenever(analyticsRestApi.trackEvent(any(), any(), any())).thenReturn(false)

        val eventProcessor = EventProcessor(
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao,
            isOffline = false,
            trackingId = "foo",
            clientUid = "uid"
        )

        eventProcessor.processEvent(event)

        verify(eventDao).insert(any())
    }
}
