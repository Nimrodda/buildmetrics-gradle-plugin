package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.cache.*
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
        val dbHelper: DatabaseHelper? = try {
            DatabaseHelper()
        } catch (e: Exception) {
            log.warn(e) { "Failed to connect to build metrics database" }
            null
        }
        val clientDao = createClientDao(dbHelper)
        val eventDao = createEventDao(dbHelper)
        val systemInfo = SystemInfo()
        val clientManager = ClientManager(clientDao, systemInfo, listeners)
        val client = clientManager.getOrCreateClient()
        project.afterEvaluate {
            val cacheManager = CacheManager(client, eventDao, listeners)
            cacheManager.pushCachedEvents()

            log.info { "Registering build listener" }
            project.gradle.addBuildListener(
                BuildDurationTracker(
                    listeners,
                    eventDao,
                    client,
                    systemInfo
                )
            )
        }
    }

    private fun createClientDao(dbHelper: DatabaseHelper?): ClientDao {
        return if (dbHelper != null) {
            log.debug { "Creating ClientDaoSqlite" }
            ClientDaoSqlite(dbHelper.database.clientQueries)
        } else {
            log.debug { "Creating ClientDaoNoOp" }
            ClientDaoNoOp()
        }
    }

    private fun createEventDao(dbHelper: DatabaseHelper?): EventDao {
        return if (dbHelper != null) {
            log.debug { "Creating EventDaoSqlite" }
            EventDaoSqlite(dbHelper.database.eventQueries)
        } else {
            log.debug { "Creating EventDaoNoOp" }
            EventDaoNoOp()
        }
    }
}
