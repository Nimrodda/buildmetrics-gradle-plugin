package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.cache.SQLiteEventDao
import com.nimroddayan.buildmetrics.plugin.Injection.okHttpClient
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import com.nimroddayan.buildmetrics.publisher.google.GoogleAnalyticsRestApi
import com.nimroddayan.buildmetrics.tracker.BuildDurationTracker
import com.nimroddayan.buildmetrics.tracker.User
import mu.KotlinLogging
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

private val log = KotlinLogging.logger {}

@Suppress("unused")
class BuildMetricsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("buildMetrics", BuildMetricsExtension::class.java)

        log.debug { "Registering build listener" }
        project.gradle.addBuildListener(
            BuildDurationTracker(
                extension,
                User("555", "Nimrod"),
                GoogleAnalyticsRestApi(okHttpClient),
                SQLiteEventDao()
            )
        )
    }
}

open class BuildMetricsExtension(objectFactory: ObjectFactory) {
    val trackingId: Property<String> = objectFactory.property(String::class.java)
    val analyticsRestApi: Property<AnalyticsRestApi> = objectFactory.property(AnalyticsRestApi::class.java)
}
