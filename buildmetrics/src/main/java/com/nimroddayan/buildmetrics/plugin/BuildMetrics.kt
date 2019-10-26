package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.cache.SQLiteEventDao
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import com.nimroddayan.buildmetrics.publisher.google.GoogleAnalyticsRestApi
import com.nimroddayan.buildmetrics.tracker.BuildDurationTracker
import com.nimroddayan.buildmetrics.tracker.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

private val log = LoggerFactory.getLogger("BuildMetricsPlugin")

@Suppress("unused")
class BuildMetricsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("buildMetrics", BuildMetricsExtension::class.java)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(HttpGradleLogger()).apply { level = HttpLoggingInterceptor.Level.BODY })
            .callTimeout(3L, TimeUnit.SECONDS)
            .build()

        val analyticsRestApi =
            extension.analyticsRestApi ?: GoogleAnalyticsRestApi(
                okHttpClient
            )

        val trackingId =
            extension.trackingId ?: throw InvalidUserDataException("Missing trackingId in extension")

        log.info("Using tracking ID: $trackingId")
        log.info("Using analytics rest API: ${analyticsRestApi::class.simpleName}")

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

open class BuildMetricsExtension {
    var trackingId: String? = null
    var analyticsRestApi: AnalyticsRestApi? = null
}

private class HttpGradleLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        log.debug(message)
    }
}
