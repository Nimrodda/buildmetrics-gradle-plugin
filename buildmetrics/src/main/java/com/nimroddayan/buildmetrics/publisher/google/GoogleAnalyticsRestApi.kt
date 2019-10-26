package com.nimroddayan.buildmetrics.publisher.google

import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import com.nimroddayan.buildmetrics.tracker.Event
import mu.KotlinLogging
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val PLAIN_TEXT = "plain/text".toMediaType()

private val ANALYTICS_URL = HttpUrl.Builder()
    .scheme("https")
    .host("www.google-analytics.com")
    .addPathSegments("collect")
    .build()

private val log = KotlinLogging.logger {}

class GoogleAnalyticsRestApi(
    private val httpClient: OkHttpClient,
    private val url: HttpUrl = ANALYTICS_URL
) : AnalyticsRestApi {

    override fun trackEvent(event: Event): Boolean {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "")
            .post(event.toRequestBody())
            .build()

        log.debug { "Sending analytics to Google" }
        val response = httpClient.newCall(request).execute()
        val isSuccess = response.isSuccessful
        response.close()
        return isSuccess
    }
}

fun Event.toRequestBody(): RequestBody {
    return "v=1&tid=$trackingId&uid=$uid&t=event&ec=$category&ea=$action&el=$label&ev=$value"
        .toRequestBody(PLAIN_TEXT)
}
