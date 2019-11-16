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

package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.cache.*
import com.nimroddayan.buildmetrics.clientid.ClientManager
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.tracker.BuildDurationTracker
import mu.KotlinLogging
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty
import oshi.SystemInfo
import java.util.concurrent.CopyOnWriteArraySet

private val log = KotlinLogging.logger {}

@Suppress("unused")
class BuildMetricsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("buildMetrics", BuildMetricsExtensions::class.java, project.objects)

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
            val extension = project.extensions.getByType(BuildMetricsExtensions::class.java)
            val taskFilter = extension.taskFilter.orNull ?: emptySet()
            val listeners = extension.buildMetricsListeners
            log.debug { "Task filter: $taskFilter" }
            clientManager.notifyClientCreated(client, listeners)
            cacheManager.pushCachedEvents(client, listeners)

            log.info { "Registering build listener" }
            project.gradle.addBuildListener(
                BuildDurationTracker(
                    listeners,
                    eventDao,
                    client,
                    systemInfo,
                    taskFilter
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

abstract class BuildMetricsExtensions(objectFactory: ObjectFactory) {
    private val _listeners = CopyOnWriteArraySet<BuildMetricsListener>()
    @Suppress("MemberVisibilityCanBePrivate")
    val buildMetricsListeners: Set<BuildMetricsListener>
        get() = _listeners

    /**
     * A set of tasks which should be tracked. If left empty, all tasks will be tracked.
     *
     * The tasks contained in this set must be the tasks that starts the build.
     */
    @Suppress("unused")
    val taskFilter: SetProperty<String> = objectFactory.setProperty(String::class.java)

    /**
     * Register a [BuildMetricsListener] which will be called when the build is finished
     */
    fun register(buildMetricsListener: BuildMetricsListener) {
        log.debug { "Register build metrics listener: $buildMetricsListener" }
        _listeners.add(buildMetricsListener)
    }
}
