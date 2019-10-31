package com.nimroddayan.buildmetrics

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.tracker.EventProcessor
import com.nimroddayan.buildmetrics.publisher.Client
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class EventProcessorTest {

    @Mock
    private lateinit var buildMetricsListener: BuildMetricsListener

    @Mock
    private lateinit var eventDao: EventDao

    private val event = BuildFinishedEvent(true, 0L, 0L, 0L)
    private val client = Client("", "", "", "", 1L, "")
    private lateinit var listeners: List<BuildMetricsListener>

    @Before
    fun setup() {
        listeners = listOf(buildMetricsListener)
    }

    @Test
    fun `track build finished when online calls listeners successfully`() {
        val eventProcessor = EventProcessor(
            listeners = listeners,
            eventDao = eventDao,
            isOffline = false,
            client = client
        )

        eventProcessor.processEvent(event)

        verifyZeroInteractions(eventDao)
    }

    @Test
    fun `track build finished should cache locally when offline`() {
        val eventProcessor = EventProcessor(
            listeners = listeners,
            eventDao = eventDao,
            isOffline = true,
            client = client
        )

        eventProcessor.processEvent(event)

        verify(eventDao).insert(any())
        verifyZeroInteractions(buildMetricsListener)
    }

    @Test
    fun `track build finished calls listeners throws exception should fallback to local cache`() {
        whenever(buildMetricsListener.onBuildFinished(any(), any())).thenThrow(RuntimeException::class.java)

        val eventProcessor = EventProcessor(
            listeners = listeners,
            eventDao = eventDao,
            isOffline = false,
            client = client
        )

        eventProcessor.processEvent(event)

        verify(eventDao).insert(any())
    }
}
