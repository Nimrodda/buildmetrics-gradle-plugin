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

package com.nimroddayan.buildmetrics.publisher

/**
 * Data class representing info regarding the client that ran the build
 *
 * @param id Unique identifier. Created locally.
 * @param osName The name of the Operating System running the build
 * @param osVersion The version of the Operating System running the build
 * @param cpu The client's processor info. This is the commercial name of the processor. For example: "Intel Core i7 8700K ..."
 * @param ram The client's total RAM capacity. This is a number with RAM unit, such as "16 GB", "32 GB", etc.
 * @param model The commercial name of the client machine model. For example "Macbook Pro ...".
 */
data class Client(
    val id: String,
    val osName: String,
    val osVersion: String,
    val cpu: String,
    val ram: String,
    val model: String,
    internal val synced: Boolean = false
)
