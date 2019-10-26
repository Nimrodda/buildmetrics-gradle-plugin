package com.nimroddayan.buildmetrics

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import com.nimroddayan.buildmetrics.tracker.BuildDurationTracker
import com.nimroddayan.buildmetrics.tracker.User
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BuildDurationTrackerTest {

    @Mock
    private lateinit var analyticsRestApi: AnalyticsRestApi

    @Mock
    private lateinit var eventDao: EventDao

    @Test
    fun `track build finished when online calls rest api`() {
        whenever(analyticsRestApi.trackEvent(any())).thenReturn(true)

        val tracker = BuildDurationTracker(
            trackingId = "foo",
            user = User(uid = "555", name = "Foo"),
            isOffline = false,
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao
        )

        tracker.trackBuildFinished(true, 40L)

        verifyZeroInteractions(eventDao)
    }

    @Test
    fun `track build finished when offline calls eventDao`() {
        val tracker = BuildDurationTracker(
            trackingId = "foo",
            user = User(uid = "555", name = "Foo"),
            isOffline = true,
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao
        )

        tracker.trackBuildFinished(true, 40L)

        verify(eventDao).insert(any())
        verifyZeroInteractions(analyticsRestApi)
    }

    @Test
    fun `track build finished call to rest api throws fallback to eventDao`() {
        whenever(analyticsRestApi.trackEvent(any())).thenThrow(RuntimeException::class.java)

        val tracker = BuildDurationTracker(
            trackingId = "UA-150617560-1",
            user = User(uid = "555", name = "Foo"),
            isOffline = false,
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao
        )

        tracker.trackBuildFinished(true, 40L)

        verify(eventDao).insert(any())
    }

    @Test
    fun `track build finished call to rest api fails fallback to eventDao`() {
        whenever(analyticsRestApi.trackEvent(any())).thenReturn(false)

        val tracker = BuildDurationTracker(
            trackingId = "UA-150617560-1",
            user = User(uid = "555", name = "Foo"),
            isOffline = false,
            analyticsRestApi = analyticsRestApi,
            eventDao = eventDao
        )

        tracker.trackBuildFinished(true, 40L)

        verify(eventDao).insert(any())
    }
}
