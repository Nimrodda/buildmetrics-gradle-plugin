package com.nimroddayan.buildmetrics.tracker

import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import mu.KotlinLogging
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.GradleInternal
import org.gradle.api.invocation.Gradle
import org.gradle.internal.scan.time.BuildScanBuildStartedTime
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

class BuildDurationTracker(
    private val trackingId: String,
    private val user: User,
    private val isOffline: Boolean,
    private val analyticsRestApi: AnalyticsRestApi,
    private val eventDao: EventDao
) : BuildListener {
    private var buildStart: Long = 0

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
        buildStart = (gradle as GradleInternal)
            .services.get(BuildScanBuildStartedTime::class.java)
            ?.buildStartedTime ?: System.currentTimeMillis()
        log.debug { "Build started: $buildStart" }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun trackBuildFinished(isSuccessful: Boolean, buildDuration: Long) {
        val event = Event(
            trackingId = trackingId,
            uid = user.uid,
            category = "Build",
            action = "Finished",
            label = if (isSuccessful) "Success" else "Failure",
            value = "$buildDuration"
        )
        processEvent(event)
    }

    private fun processEvent(event: Event) {
        log.debug { "Processing event: $event" }
        try {
            if (isOffline || !analyticsRestApi.trackEvent(event)) {
                log.debug { "User in offline or request failed, caching analytics in local database" }
                eventDao.insert(event)
            }
        } catch (e: Exception) {
            log.debug(e) { "Request to analytics tracker failed, caching analytics in local database" }
            eventDao.insert(event)
        }
    }
}
