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
            log.debug { "Attempting to create database" }
            BuildMetricsDb.Schema.create(driver)
            log.debug { "Database created successfully, returning database instance" }
            BuildMetricsDb(driver)
        } catch (e: Exception) {
            log.debug { "Database already exists, returning instance" }
            BuildMetricsDb(driver)
        }
    }
}
