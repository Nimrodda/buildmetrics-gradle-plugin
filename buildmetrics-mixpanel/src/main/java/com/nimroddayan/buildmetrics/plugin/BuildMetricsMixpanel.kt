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

package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.plugin.Injection.moshi
import com.nimroddayan.buildmetrics.plugin.Injection.retrofit
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import com.nimroddayan.buildmetrics.publisher.mixpanel.MixpanelRestApi
import mu.KotlinLogging
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

private val log = KotlinLogging.logger {}

@Suppress("unused")
class BuildMetricsMixpanelPlugin : Plugin<Project>, BuildMetricsListener {
    private lateinit var mixpanelRestApi: MixpanelRestApi

    override fun onClientCreated(client: Client) {
        log.debug { "Updating user profile with Mixpanel" }
        mixpanelRestApi.updateProfile(client)
    }

    override fun onBuildFinished(client: Client, event: BuildFinishedEvent) {
        log.debug { "Tracking event with Mixpanel" }
        mixpanelRestApi.trackBuildFinishedEvent(client, event)
    }

    @Suppress("UnstableApiUsage")
    override fun apply(project: Project) {
        log.debug { "Initializing Mixpanel Build Metrics plugin" }
        project.pluginManager.apply(BuildMetricsPlugin::class.java)
        val extension = project.extensions.create("mixpanel", BuildMetricsMixpanelAnalyticsExtension::class.java, project.objects)
        project.extensions.getByType(BuildMetricsExtensions::class.java).register(this)
        mixpanelRestApi = MixpanelRestApi(retrofit, moshi, extension.token)
    }
}

@Suppress("unused", "UnstableApiUsage")
abstract class BuildMetricsMixpanelAnalyticsExtension(objectFactory: ObjectFactory) {
    val token: Property<String> = objectFactory.property(String::class.java)
}
