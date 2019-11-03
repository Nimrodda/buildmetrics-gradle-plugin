package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.cache.ClientDaoSqlite
import com.nimroddayan.buildmetrics.cache.DatabaseHelper
import com.nimroddayan.buildmetrics.cache.EventDaoSqlite
import com.nimroddayan.buildmetrics.clientid.ClientManager
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.tracker.BuildDurationTracker
import mu.KotlinLogging
import org.gradle.api.Plugin
import org.gradle.api.Project
import oshi.SystemInfo

private val log = KotlinLogging.logger {}

@Suppress("unused")
class BuildMetricsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val listeners = mutableListOf<BuildMetricsListener>()
        log.info { "Registering all build metrics listeners" }
        project.plugins.all {
            if (it is BuildMetricsListener) {
                log.debug { "Registering listener: ${it::class.simpleName}" }
                listeners += it
            }
        }
        val systemInfo = SystemInfo()
        val dbHelper = DatabaseHelper()
        val clientManager = ClientManager(ClientDaoSqlite(dbHelper.database.clientQueries), systemInfo, listeners)

        log.info { "Registering build listener" }
        project.gradle.addBuildListener(
            BuildDurationTracker(
                listeners,
                EventDaoSqlite(dbHelper.database.eventQueries),
                clientManager.getOrCreateClient(),
                systemInfo
            )
        )
    }
}
