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

import com.squareup.moshi.Json

internal data class BuildFinishedEventRequest(
    @Json(name = "event") val event: String,
    @Json(name = "properties") val properties: BuildFinishedEventProperties
)

internal class BuildFinishedEventProperties(
    @Json(name = "build_success") val isBuildSuccess: Boolean,
    @Json(name = "build_duration") val buildDuration: Long,
    @Json(name = "build_free_ram") val buildFreeRam: String,
    @Json(name = "build_swap_ram") val buildSwapRam: String,
    @Json(name = "build_task_names") val buildTaskNames: String,
    distinctId: String,
    token: String
) : MixpanelEventProperties(distinctId, token)
