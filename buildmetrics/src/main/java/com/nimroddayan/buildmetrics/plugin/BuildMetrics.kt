package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.cache.SQLiteEventDao
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import com.nimroddayan.buildmetrics.publisher.google.GoogleAnalyticsRestApi
import com.nimroddayan.buildmetrics.tracker.BuildDurationTracker
import com.nimroddayan.buildmetrics.tracker.User
import okhttp3.OkHttpClient
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.concurrent.TimeUnit

@Suppress("unused")
class BuildMetricsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension =
            project.extensions.create("buildMetrics", BuildMetricsExtension::class.java, project)

        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(3L, TimeUnit.SECONDS)
            .build()

        val analyticsRestApi =
            extension.analyticsRestApi ?: GoogleAnalyticsRestApi(
                okHttpClient
            )

        val trackingId =
            extension.trackingId ?: throw GradleException("Missing trackingId in extension")

        project.gradle.addBuildListener(
            BuildDurationTracker(
                trackingId,
                User("555", "Nimrod"),
                project.gradle.startParameter.isOffline,
                analyticsRestApi,
                SQLiteEventDao()
            )
        )
    }
}

open class BuildMetricsExtension(@Suppress("UNUSED_PARAMETER") project: Project) {
    var trackingId: String? = null
    var analyticsRestApi: AnalyticsRestApi? = null
}
