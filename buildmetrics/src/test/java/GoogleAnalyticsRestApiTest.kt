package com.nimroddayan.buildmetrics

import com.google.common.truth.Truth.assertThat
import com.nimroddayan.buildmetrics.publisher.google.GoogleAnalyticsRestApi
import com.nimroddayan.buildmetrics.tracker.Event
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test

class GoogleAnalyticsRestApiTest {
    @Test
    fun `valid request body`() {
        val server = MockWebServer()
        server.enqueue(MockResponse())
        server.start()
        val url = server.url("/collect/")

        GoogleAnalyticsRestApi(OkHttpClient(), url)
            .trackEvent(
                Event(
                    trackingId = "FOO",
                    uid = "555",
                    category = "Build",
                    action = "Finished",
                    label = "Success",
                    value = "40"
                )
            )

        val request = server.takeRequest()

        assertThat(request.body.readUtf8())
            .isEqualTo("v=1&tid=FOO&uid=555&t=event&ec=Build&ea=Finished&el=Success&ev=40")
    }
}
