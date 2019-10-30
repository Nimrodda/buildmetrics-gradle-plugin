package com.nimroddayan.buildmetrics.clientid

import com.nimroddayan.buildmetrics.Client
import com.nimroddayan.buildmetrics.cache.ClientDao
import mu.KotlinLogging
import oshi.SystemInfo
import java.util.UUID

private val log = KotlinLogging.logger {}

class ClientManager(
    private val clientDao: ClientDao
) {
    fun getOrCreateClient(): Client {
        return try {
            clientDao.selectFirst()
        } catch (e: Exception) {
            log.debug(e) { "Client doesn't exist, creating..." }
            val systemInfo = SystemInfo()
            val client = Client.Impl(
                id = UUID.randomUUID().toString(),
                os_name = SystemInfo.getCurrentPlatformEnum().name,
                os_version = systemInfo.operatingSystem.versionInfo.version ?: "",
                ram = systemInfo.hardware.memory.total,
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
}
