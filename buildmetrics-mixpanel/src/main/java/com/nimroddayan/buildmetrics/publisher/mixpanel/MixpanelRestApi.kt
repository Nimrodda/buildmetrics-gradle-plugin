package com.nimroddayan.buildmetrics.publisher.mixpanel

import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.Client
import com.squareup.moshi.Moshi
import mu.KotlinLogging
import okhttp3.ResponseBody
import org.gradle.api.provider.Property
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Base64
import kotlin.text.Charsets.UTF_8

const val ANALYTICS_URL = "https://api.mixpanel.com/"

private val log = KotlinLogging.logger {}

interface Mixpanel {
    @GET("track")
    fun trackEvent(@Query("data") data: String): Call<ResponseBody>

    @GET("engage")
    fun updateProfile(@Query("data") data: String): Call<ResponseBody>
}

/**
 * Mixpanel REST API for sending build finished events and profile updates
 */
class MixpanelRestApi(
    retrofit: Retrofit,
    private val moshi: Moshi,
    @Suppress("UnstableApiUsage") private val token: Property<String>
) {
    private val mixpanel = retrofit.create(Mixpanel::class.java)

    fun trackBuildFinishedEvent(client: Client, event: BuildFinishedEvent) {
        val request = event.toBuildFinishedEventRequest(client.id, token.get())
        val requestJson = moshi.adapter(BuildFinishedEventRequest::class.java).toJson(request)
        val requestBase64 = Base64.getEncoder().encodeToString(requestJson.toByteArray(UTF_8))
        log.debug { "Sending analytics to Mixpanel" }
        mixpanel.trackEvent(requestBase64).execute()
    }

    fun updateProfile(client: Client) {
        val request = client.toUpdateProfileRequest(token.get())
        val requestJson = moshi.adapter(UpdateProfileRequest::class.java).toJson(request)
        val requestBase64 = Base64.getEncoder().encodeToString(requestJson.toByteArray(UTF_8))
        log.debug { "Sending user profile update to Mixpanel" }
        mixpanel.updateProfile(requestBase64)
    }
}

fun BuildFinishedEvent.toBuildFinishedEventRequest(distinctId: String, token: String): BuildFinishedEventRequest {
    return BuildFinishedEventRequest(
        event = "BuildFinished",
        properties = BuildFinishedEventProperties(
            isBuildSuccess = isSuccess,
            buildDuration = durationSeconds,
            buildFreeRam = freeRam,
            buildSwapRam = swapRam,
            distinctId = distinctId,
            token = token
        )
    )
}

fun Client.toUpdateProfileRequest(token: String): UpdateProfileRequest {
    return UpdateProfileRequest(
        token = token,
        distinctId = id,
        clientInfo = ClientInfo(
            osName = osName,
            osVersion = osVersion,
            model = model,
            cpu = cpu,
            ram = ram
        )
    )
}
