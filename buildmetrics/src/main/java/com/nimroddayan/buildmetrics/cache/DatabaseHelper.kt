package com.nimroddayan.buildmetrics.cache

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

class DatabaseHelper(
    dbFile: String? = ".buildmetrics"
) {
    private val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile ?: ""}")
    val database = BuildMetricsDb(driver)

    init {
        BuildMetricsDb.Schema.create(driver)
    }
}
