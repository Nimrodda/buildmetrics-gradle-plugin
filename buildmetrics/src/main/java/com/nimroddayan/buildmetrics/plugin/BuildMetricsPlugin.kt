package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.cache.*
import com.nimroddayan.buildmetrics.clientid.ClientManager
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.tracker.BuildDurationTracker
import mu.KotlinLogging
import org.gradle.api.Plugin
import org.gradle.api.Project
import oshi.SystemInfo
import java.util.concurrent.CopyOnWriteArraySet

private val log = KotlinLogging.logger {}

@Suppress("unused")
class BuildMetricsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("buildMetrics", BuildMetricsExtensions::class.java)

        val dbHelper: DatabaseHelper? = try {
            DatabaseHelper()
        } catch (e: Exception) {
            log.warn(e) { "Failed to connect to build metrics database" }
            null
        }
        val clientDao = createClientDao(dbHelper)
        val eventDao = createEventDao(dbHelper)
        val systemInfo = SystemInfo()
        val clientManager = ClientManager(clientDao, systemInfo)
        val client = clientManager.getOrCreateClient()
        val cacheManager = CacheManager(eventDao)
        project.afterEvaluate {
            val listeners = project.extensions.getByType(BuildMetricsExtensions::class.java).buildMetricsListeners
            clientManager.notifyClientCreated(client, listeners)
            cacheManager.pushCachedEvents(client, listeners)

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

abstract class BuildMetricsExtensions {
    private val _listeners = CopyOnWriteArraySet<BuildMetricsListener>()
    @Suppress("MemberVisibilityCanBePrivate")
    val buildMetricsListeners: Set<BuildMetricsListener>
        get() = _listeners

    fun register(buildMetricsListener: BuildMetricsListener) {
        log.debug { "Register build metrics listener: $buildMetricsListener" }
        _listeners.add(buildMetricsListener)
    }
}
