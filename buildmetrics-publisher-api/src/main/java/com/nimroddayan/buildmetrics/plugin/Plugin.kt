package com.nimroddayan.buildmetrics.plugin

import mu.KotlinLogging
import org.gradle.api.Project

private val log = KotlinLogging.logger {}

const val BUILD_METRICS_RUNTIME_PLUGIN_ID = "com.nimroddayan.buildmetrics.runtime"

fun Project.checkBuildMetricsPluginApplied() {
    if (!pluginManager.hasPlugin(BUILD_METRICS_RUNTIME_PLUGIN_ID)) {
        log.warn { "Build Metrics plugin is not applied" }
    }
}
