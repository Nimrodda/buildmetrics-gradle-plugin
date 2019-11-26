/*
 *    Copyright 2019 Nimrod Dayan nimroddayan.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

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

internal interface Mixpanel {
    @GET("track?verbose=1")
    fun trackEvent(@Query("data") data: String): Call<ResponseBody>

    @GET("engage?verbose=1")
    fun updateProfile(@Query("data") data: String): Call<ResponseBody>
}

/**
 * Mixpanel REST API for sending build finished events and profile updates
 */
internal class MixpanelRestApi(
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
        mixpanel.updateProfile(requestBase64).execute()
    }
}

internal fun BuildFinishedEvent.toBuildFinishedEventRequest(distinctId: String, token: String): BuildFinishedEventRequest {
    return BuildFinishedEventRequest(
        event = "BuildFinished",
        properties = BuildFinishedEventProperties(
            isBuildSuccess = isSuccess,
            buildDuration = durationSeconds,
            buildFreeRam = freeRam,
            buildSwapRam = swapRam,
            distinctId = distinctId,
            token = token,
            buildTaskNames = taskNames
        )
    )
}

internal fun Client.toUpdateProfileRequest(token: String): UpdateProfileRequest {
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
