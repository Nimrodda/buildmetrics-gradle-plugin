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

package com.nimroddayan.buildmetrics.publisher.amplitude

import com.squareup.moshi.Json

internal data class BuildFinishedEventRequest(
    @Json(name = "api_key") val apiKey: String,
    @Json(name = "events") val events: List<BuildFinishedEventType>
)

internal data class BuildFinishedEventType(
    @Json(name = "user_id") val userId: String,
    @Json(name = "event_type") val eventType: String,
    @Json(name = "time") val time: Long,
    @Json(name = "event_properties") val eventProperties: BuildFinishedEventProperties,
    @Json(name = "user_properties") val userProperties: UserProperties,
    @Json(name = "os_name") val osName: String,
    @Json(name = "os_version") val osVersion: String,
    @Json(name = "device_model") val model: String
)

internal class BuildFinishedEventProperties(
    @Json(name = "build_success") val isBuildSuccess: Boolean,
    @Json(name = "build_duration") val buildDuration: Long,
    @Json(name = "build_free_ram") val buildFreeRam: String,
    @Json(name = "build_swap_ram") val buildSwapRam: String,
    @Json(name = "build_task_names") val buildTaskNames: String
)

internal data class UserProperties(
    @Json(name = "cpu") val cpu: String,
    @Json(name = "ram") val ram: String
)
