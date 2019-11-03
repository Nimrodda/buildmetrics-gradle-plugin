package com.nimroddayan.buildmetrics.clientid

import com.nimroddayan.buildmetrics.cache.ClientDao
import com.nimroddayan.buildmetrics.publisher.BuildMetricsListener
import com.nimroddayan.buildmetrics.publisher.Client
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import oshi.SystemInfo
import java.util.UUID

private val log = KotlinLogging.logger {}

class ClientManager(
    private val clientDao: ClientDao,
    private val systemInfo: SystemInfo,
    private val buildMetricsListeners: List<BuildMetricsListener>
) {
    fun getOrCreateClient(): Client {
        return try {
            clientDao.selectFirst()
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
                log.debug { "Notifying listeners that a client has been created" }
                try {
                    buildMetricsListeners.forEach { it.onClientCreated(client) }
                } catch (e: Exception) {
                    log.debug { "Error notifying listeners that a client was created" }
                }
                client
            } catch (e: Exception) {
                log.debug(e) { "Failed to store client in local database, returning in-memory client" }
                client
            }
        }
    }
}
