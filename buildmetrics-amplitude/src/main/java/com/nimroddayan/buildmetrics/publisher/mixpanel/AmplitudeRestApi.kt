package com.nimroddayan.buildmetrics.publisher.mixpanel

import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.Client
import mu.KotlinLogging
import okhttp3.ResponseBody
import org.gradle.api.provider.Property
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

const val ANALYTICS_URL = "https://api.amplitude.com/"

private val log = KotlinLogging.logger {}

interface Amplitude {
    @POST("2/httpapi")
    fun trackEvent(@Body request: BuildFinishedEventRequest): Call<ResponseBody>
}

/**
 * Amplitude REST API for sending build finished events and user properties
 */
class AmplitudeRestApi(
    retrofit: Retrofit,
    @Suppress("UnstableApiUsage") private val apiKey: Property<String>
) {
    private val mixpanel = retrofit.create(Amplitude::class.java)

    fun trackBuildFinishedEvent(client: Client, event: BuildFinishedEvent) {
        val request = event.toBuildFinishedEventRequest(client, apiKey.get())
        log.debug { "Sending analytics to Amplitude" }
        mixpanel.trackEvent(request).execute()
    }
}

fun BuildFinishedEvent.toBuildFinishedEventRequest(client: Client, apiKey: String): BuildFinishedEventRequest {
    return BuildFinishedEventRequest(
        apiKey = apiKey,
        events = listOf(
            BuildFinishedEventType(
                userId = client.id,
                eventType = "BuildFinished",
                time = this.timestamp,
                eventProperties = BuildFinishedEventProperties(
                    isBuildSuccess = this.isSuccess,
                    buildDuration = this.durationSeconds,
                    buildFreeRam = this.freeRam,
                    buildSwapRam = this.swapRam,
                    buildTaskNames = this.taskNames
                ),
                userProperties = UserProperties(
                    cpu = client.cpu,
                    ram = client.ram
                ),
                osName = client.osName,
                osVersion = client.osVersion,
                model = client.model
            )
        )
    )
}
