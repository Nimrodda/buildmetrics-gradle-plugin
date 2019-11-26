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

package com.nimroddayan.buildmetrics.clientid

import com.nimroddayan.buildmetrics.cache.ClientDao
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import oshi.SystemInfo
import java.util.UUID

private val log = KotlinLogging.logger {}

internal class ClientManager(
    private val clientDao: ClientDao,
    private val systemInfo: SystemInfo
) {
    fun getOrCreateClient(): Client {
        return try {
            val client = clientDao.selectFirst()
            client
        } catch (e: Exception) {
            log.debug(e) { "Client doesn't exist, creating..." }
            val client = Client(
                id = UUID.randomUUID().toString(),
                osName = SystemInfo.getCurrentPlatformEnum().name,
                osVersion = systemInfo.operatingSystem.versionInfo.version ?: "",
                ram = FileUtils.byteCountToDisplaySize(systemInfo.hardware.memory.total),
                cpu = systemInfo.hardware.processor.processorIdentifier.name,
                model = systemInfo.hardware.computerSystem.model
            )
            log.info { "Created client: $client" }
            try {
                log.debug { "Storing client in local database" }
                clientDao.deleteAll()
                clientDao.insert(client)
                client
            } catch (e: Exception) {
                log.debug(e) { "Failed to store client in local database, returning in-memory client" }
                client
            }
        }
    }

    fun notifyClientCreated(client: Client, buildMetricsListeners: Set<BuildMetricsListener>) {
        if (client.synced || buildMetricsListeners.isEmpty()) return
        try {
            log.debug { "Notifying listeners that a client has been created" }
            buildMetricsListeners.forEach { it.onClientCreated(client) }
            clientDao.markSynced()
        } catch (e: Exception) {
            log.debug { "Error notifying listeners that a client was created" }
        }
    }
}
