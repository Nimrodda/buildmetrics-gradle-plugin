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

package com.nimroddayan.buildmetrics.tracker

import com.nimroddayan.buildmetrics.cache.EventDao
import com.nimroddayan.buildmetrics.publisher.BuildFinishedEvent
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.GradleInternal
import org.gradle.api.invocation.Gradle
import org.gradle.internal.scan.time.BuildScanBuildStartedTime
import oshi.SystemInfo
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

class BuildDurationTracker(
    private val listeners: Set<BuildMetricsListener>,
    private val eventDao: EventDao,
    private val client: Client,
    private val systemInfo: SystemInfo,
    private val taskFilter: Set<String>
) : BuildListener {
    private var buildStart: Long = 0L
    private var isTracking = true
    private val taskNames = mutableListOf<String>()
    private lateinit var eventProcessor: EventProcessor

    override fun settingsEvaluated(gradle: Settings) {
    }

    override fun buildFinished(buildResult: BuildResult) {
        if (!isTracking) return

        val duration = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - buildStart)
        trackBuildFinished(buildResult.failure == null, duration, taskNames)
    }

    override fun projectsLoaded(gradle: Gradle) {
    }

    override fun buildStarted(gradle: Gradle) {
    }

    override fun projectsEvaluated(gradle: Gradle) {
        val taskNames = gradle.startParameter.taskNames
        log.debug { "Build start parameter tasks: $taskNames" }
        val task = taskNames.first().split(":").last()
        isTracking = taskFilter.isEmpty() || taskFilter.any { task.startsWith(it) }
        if (!isTracking) return

        log.info { "Tracking build duration..." }
        this.taskNames += taskNames
        eventProcessor = EventProcessor(
            gradle.startParameter.isOffline,
            eventDao,
            client,
            listeners
        )

        buildStart = (gradle as GradleInternal)
            .services.get(BuildScanBuildStartedTime::class.java)
            ?.buildStartedTime ?: System.currentTimeMillis()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun trackBuildFinished(isSuccessful: Boolean, buildDuration: Long, taskNames: List<String>) {
        val event = BuildFinishedEvent(
            freeRam = FileUtils.byteCountToDisplaySize(systemInfo.hardware.memory.available),
            isSuccess = isSuccessful,
            durationSeconds = buildDuration,
            swapRam = FileUtils.byteCountToDisplaySize(systemInfo.hardware.memory.virtualMemory.swapUsed),
            taskNames = taskNames.joinToString()
        )
        eventProcessor.processEvent(event)
    }
}
