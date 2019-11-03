package com.nimroddayan.buildmetrics.publisher.google

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.whenever
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.Client
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.gradle.api.provider.Property
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GoogleAnalyticsRestApiTest {
    @Suppress("UnstableApiUsage")
    @Mock
    private lateinit var trackingId: Property<String>

    @Test
    fun `valid request body`() {
        whenever(trackingId.get()).thenReturn("trackingId")
        val server = MockWebServer()
        server.enqueue(MockResponse())
        server.start()
        val url = server.url("/collect/")
        val clientId = "clientId"
        val isSuccess = true
        val freeRam = "12 GB"
        val durationSeconds = 14L
        val swapRam = "2 GB"
        GoogleAnalyticsRestApi(OkHttpClient(), trackingId, url)
            .trackBuildFinishedEvent(
                client = Client(clientId, "osname", "osversion", "cpu", "16 GB", "model"),
                event = BuildFinishedEvent(isSuccess, durationSeconds, freeRam, swapRam)
            )

        val request = server.takeRequest()

        assertThat(request.body.readUtf8())
            .isEqualTo("v=1&tid=$trackingId&cid=$clientId&t=event&ec=Build&" +
                "ea=Finished&el=isSuccess:$isSuccess,ram:$freeRam,swap:$swapRam&ev=$durationSeconds")
    }
}
