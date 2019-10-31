package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.plugin.Injection.okHttpClient
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import com.nimroddayan.buildmetrics.publisher.google.GoogleAnalyticsRestApi
import mu.KotlinLogging
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

private val log = KotlinLogging.logger {}

class BuildMetricsGoogleAnalyticsPlugin : Plugin<Project>, BuildMetricsListener {
    private lateinit var googleAnalyticsRestApi: GoogleAnalyticsRestApi

    override fun onBuildFinished(client: Client, event: BuildFinishedEvent) {
        log.debug { "Tracking event with Google Analytics" }
        googleAnalyticsRestApi.trackBuildFinishedEvent(client, event)
    }

    override fun apply(project: Project) {
        log.debug { "Initializing Google Analytics Build Metrics plugin" }
        val extension = project.extensions.create("googleAnalytics", BuildMetricsGoogleAnalyticsExtension::class.java)
        googleAnalyticsRestApi = GoogleAnalyticsRestApi(okHttpClient, extension.trackingId)
    }
}

@Suppress("unused", "UnstableApiUsage")
open class BuildMetricsGoogleAnalyticsExtension(objectFactory: ObjectFactory) {
    val trackingId: Property<String> = objectFactory.property(String::class.java)
}
