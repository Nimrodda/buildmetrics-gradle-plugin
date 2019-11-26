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

import com.nimroddayan.buildmetrics.plugin.BuildMetricsExtensions
import java.io.Serializable

/**
 * Listener for build metric events
 *
 * This interface is used by build metrics extension plugins for implementing publishers.
 * Register implementations with [BuildMetricsExtensions] in your plugin's apply method.
 *
 * For example:
 *
 * ```
 * class BuildMetricsAmplitudePlugin : Plugin<Project>, BuildMetricsListener {
 *     override fun onClientCreated(client: Client) {
 *     }
 *
 *     override fun onBuildFinished(client: Client, event: BuildFinishedEvent) {
 *     }
 *
 *     override fun apply(project: Project) {
 *         project.extensions.getByType(BuildMetricsExtensions::class.java).register(this)
 *     }
 * }
 * ```
 */
interface BuildMetricsListener : Serializable {
    /**
     * Event called when the build is finished
     */
    fun onBuildFinished(client: Client, event: BuildFinishedEvent)

    /**
     * Event called when a client was created
     *
     * This event is called only once per client unless the client deletes the database file
     */
    fun onClientCreated(client: Client)
}
