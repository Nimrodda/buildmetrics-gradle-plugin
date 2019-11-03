package com.nimroddayan.buildmetrics.plugin

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project

const val BUILD_METRICS_PLUGIN_ID = "com.nimroddayan.gradle.build.metrics"
const val BUILD_METRICS_EXTENSION_NAME = "buildMetrics"

fun Project.checkBuildMetricsPluginApplied() {
    if (!pluginManager.hasPlugin(BUILD_METRICS_PLUGIN_ID)) {
        throw GradleException("Build Metrics plugin is not applied")
    }
}

@Suppress("UnstableApiUsage")
fun <T>Project.addBuildMetricsPublisherExtension(action: Action<out T>) {
    extensions.configure(BUILD_METRICS_EXTENSION_NAME, action)
}
