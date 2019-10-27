package com.nimroddayan.buildmetrics.clientid

import com.nimroddayan.buildmetrics.Client
import com.nimroddayan.buildmetrics.cache.ClientDao
import mu.KotlinLogging
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
            val client = Client.Impl(UUID.randomUUID().toString())
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
