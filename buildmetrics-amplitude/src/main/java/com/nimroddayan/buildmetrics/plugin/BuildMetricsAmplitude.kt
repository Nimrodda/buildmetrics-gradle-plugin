package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.plugin.Injection.retrofit
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import com.nimroddayan.buildmetrics.publisher.mixpanel.AmplitudeRestApi
import mu.KotlinLogging
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

private val log = KotlinLogging.logger {}

@Suppress("unused")
class BuildMetricsAmplitudePlugin : Plugin<Project>, BuildMetricsListener {
    private lateinit var amplitudeRestApi: AmplitudeRestApi

    override fun onClientCreated(client: Client) {
    }

    override fun onBuildFinished(client: Client, event: BuildFinishedEvent) {
        log.debug { "Tracking event with Amplitude" }
        amplitudeRestApi.trackBuildFinishedEvent(client, event)
    }

    @Suppress("UnstableApiUsage")
    override fun apply(project: Project) {
        log.debug { "Initializing Amplitude Build Metrics plugin" }
        project.pluginManager.apply(BuildMetricsPlugin::class.java)
        val extension = project.extensions.create("amplitude", BuildMetricsAmplitudeAnalyticsExtension::class.java, project.objects)
        project.extensions.getByType(BuildMetricsExtensions::class.java).register(this)
        amplitudeRestApi = AmplitudeRestApi(retrofit, extension.apiKey)
    }
}

@Suppress("unused", "UnstableApiUsage")
abstract class BuildMetricsAmplitudeAnalyticsExtension(objectFactory: ObjectFactory) {
    val apiKey: Property<String> = objectFactory.property(String::class.java)
}
