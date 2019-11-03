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

class BuildMetricsMixpanelPlugin : Plugin<Project>, BuildMetricsListener {
    private lateinit var mixpanelRestApi: MixpanelRestApi

    override fun onBuildFinished(client: Client, event: BuildFinishedEvent) {
        log.debug { "Tracking event with Mixpanel" }
        mixpanelRestApi.trackBuildFinishedEvent(client, event)
    }

    override fun apply(project: Project) {
        project.checkBuildMetricsPluginApplied()
        log.debug { "Initializing Mixpanel Build Metrics plugin" }
        val extension = project.extensions.create("mixpanel", BuildMetricsGoogleAnalyticsExtension::class.java)
        mixpanelRestApi = MixpanelRestApi(retrofit, moshi, extension.token)
    }
}

@Suppress("unused", "UnstableApiUsage")
open class BuildMetricsGoogleAnalyticsExtension(objectFactory: ObjectFactory) {
    val token: Property<String> = objectFactory.property(String::class.java)
}