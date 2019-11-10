package com.nimroddayan.buildmetrics.cache

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class DatabaseHelper(
    dbFile: String? = ".buildmetrics"
) {
    val database: BuildMetricsDb

    init {
        Class.forName("org.sqlite.JDBC") // fix for "no driver found"
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile ?: ""}")
        database = try {
            log.debug { "Instantiating database instance" }
            BuildMetricsDb(driver)
        } catch (e: Exception) {
            log.debug { "Failed to instantiate database instance, attempting to create it" }
            BuildMetricsDb.Schema.create(driver)
            BuildMetricsDb(driver)
        }
    }
}
