package com.nimroddayan.buildmetrics.publisher.google

import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.Client
import mu.KotlinLogging
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.gradle.api.provider.Property

private val PLAIN_TEXT = "plain/text".toMediaType()

private val ANALYTICS_URL = HttpUrl.Builder()
    .scheme("https")
    .host("www.google-analytics.com")
    .addPathSegments("collect")
    .build()

private val log = KotlinLogging.logger {}

/**
 * Google Analytics REST API client for sending build finished events
 *
 * https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide
 */
class GoogleAnalyticsRestApi(
    private val httpClient: OkHttpClient,
    @Suppress("UnstableApiUsage") private val trackingId: Property<String>,
    private val url: HttpUrl = ANALYTICS_URL
) {
    fun trackBuildFinishedEvent(client: Client, event: BuildFinishedEvent) {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "")
            .post(event.toRequestBody(trackingId.get(), client.id))
            .build()

        log.debug { "Sending analytics to Google" }
        val response = httpClient.newCall(request).execute()
        response.close()
    }
}

fun BuildFinishedEvent.toRequestBody(trackingId: String, clientId: String): RequestBody {
    return ("v=1&tid=$trackingId&cid=$clientId&t=event&ec=Build&ea=Finished&" +
        "el=isSuccess:$isSuccess,ram:$freeRam,swap:$swapRam&ev=$durationSeconds")
        .toRequestBody(PLAIN_TEXT)
}
