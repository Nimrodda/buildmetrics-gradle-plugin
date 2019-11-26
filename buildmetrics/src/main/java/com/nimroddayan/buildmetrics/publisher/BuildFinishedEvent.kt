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
 * Data class representing a single build finished event.
 *
 * @param isSuccess true if the build was successful
 * @param durationSeconds The duration of the build in seconds
 * @param freeRam The amount of free ram when the build was finished
 * @param swapRam The amount of swap ram when the build was finished
 * @param taskNames The tasks that started the build. Including project. Example: ':app:assembleDebug'.
 * @param timestamp Epoch ms when the build was finished
 */
data class BuildFinishedEvent(
    val isSuccess: Boolean,
    val durationSeconds: Long,
    val freeRam: String,
    val swapRam: String,
    val taskNames: String,
    val timestamp: Long = System.currentTimeMillis()
)
