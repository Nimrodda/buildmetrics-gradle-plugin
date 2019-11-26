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

package com.nimroddayan.buildmetrics.cache

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

internal class DatabaseHelper(
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
