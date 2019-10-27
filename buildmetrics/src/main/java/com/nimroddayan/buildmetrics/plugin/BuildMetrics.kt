package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.cache.ClientDaoSqlite
import com.nimroddayan.buildmetrics.cache.DatabaseHelper
import com.nimroddayan.buildmetrics.cache.EventDaoSqlite
import com.nimroddayan.buildmetrics.clientid.ClientManager
import com.nimroddayan.buildmetrics.plugin.Injection.okHttpClient
import com.nimroddayan.buildmetrics.publisher.AnalyticsRestApi
import com.nimroddayan.buildmetrics.publisher.google.GoogleAnalyticsRestApi
import com.nimroddayan.buildmetrics.tracker.BuildDurationTracker
import mu.KotlinLogging
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

private val log = KotlinLogging.logger {}

@Suppress("unused")
class BuildMetricsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("buildMetrics", BuildMetricsExtension::class.java)
        val dbHelper = DatabaseHelper()
        val clientManager = ClientManager(ClientDaoSqlite(dbHelper.database.clientQueries))

        log.debug { "Registering build listener" }
        project.gradle.addBuildListener(
            BuildDurationTracker(
                extension,
                GoogleAnalyticsRestApi(okHttpClient),
                EventDaoSqlite(dbHelper.database.eventQueries),
                clientManager.getOrCreateClient().id
            )
        )
    }
}

open class BuildMetricsExtension(objectFactory: ObjectFactory) {
    val trackingId: Property<String> = objectFactory.property(String::class.java)
    val analyticsRestApi: Property<AnalyticsRestApi> = objectFactory.property(AnalyticsRestApi::class.java)
}
