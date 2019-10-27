package com.nimroddayan.buildmetrics.tracker

import com.nimroddayan.buildmetrics.Event
import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.plugin.BuildMetricsExtension
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import mu.KotlinLogging
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.InvalidUserDataException
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.GradleInternal
import org.gradle.api.invocation.Gradle
import org.gradle.internal.scan.time.BuildScanBuildStartedTime
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

class BuildDurationTracker(
    private val extension: BuildMetricsExtension,
    private val analyticsRestApi: AnalyticsRestApi,
    private val eventDao: EventDao,
    private val clientUid: String
) : BuildListener {
    private var buildStart: Long = 0
    private lateinit var eventProcessor: EventProcessor
    private lateinit var trackingId: String

    override fun settingsEvaluated(gradle: Settings) {
    }

    override fun buildFinished(buildResult: BuildResult) {
        val duration = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - buildStart)
        log.debug { "Measured build duration: $duration" }
        trackBuildFinished(buildResult.failure == null, duration)
    }

    override fun projectsLoaded(gradle: Gradle) {
    }

    override fun buildStarted(gradle: Gradle) {
    }

    override fun projectsEvaluated(gradle: Gradle) {
        trackingId = extension.trackingId.orNull ?: throw InvalidUserDataException("Missing trackingId in extension")

        eventProcessor = EventProcessor(
            gradle.startParameter.isOffline,
            eventDao,
            clientUid,
            trackingId,
            extension.analyticsRestApi.getOrElse(analyticsRestApi)
        )

        buildStart = (gradle as GradleInternal)
            .services.get(BuildScanBuildStartedTime::class.java)
            ?.buildStartedTime ?: System.currentTimeMillis()
        log.debug { "Build started: $buildStart" }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun trackBuildFinished(isSuccessful: Boolean, buildDuration: Long) {
        val event = Event.Impl(
            id = -1, // ID ignored
            category = "Build",
            action = "Finished",
            label = if (isSuccessful) "Success" else "Failure",
            value = "$buildDuration"
        )
        eventProcessor.processEvent(event)
    }
}
