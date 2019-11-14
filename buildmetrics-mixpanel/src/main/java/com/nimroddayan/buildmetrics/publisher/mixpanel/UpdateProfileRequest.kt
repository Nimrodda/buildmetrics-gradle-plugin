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

data class UpdateProfileRequest(
    @Json(name = "\$token") val token: String,
    @Json(name = "\$distinct_id") val distinctId: String,
    @Json(name = "\$set") val clientInfo: ClientInfo
)

data class ClientInfo(
    @Json(name = "os_name") val osName: String,
    @Json(name = "os_version") val osVersion: String,
    @Json(name = "cpu") val cpu: String,
    @Json(name = "ram") val ram: String,
    @Json(name = "model") val model: String
)
