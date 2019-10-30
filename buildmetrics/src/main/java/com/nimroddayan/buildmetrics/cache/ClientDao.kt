package com.nimroddayan.buildmetrics.cache

import com.nimroddayan.buildmetrics.Client
import com.nimroddayan.buildmetrics.ClientQueries

interface ClientDao {
    fun insert(client: Client)
    fun selectFirst(): Client
    fun deleteAll()
}

class ClientDaoSqlite(
    private val clientQueries: ClientQueries
) : ClientDao {
    override fun insert(client: Client) {
        clientQueries.insert(
            id = client.id,
            os_name = client.os_name,
            os_version = client.os_version,
            cpu = client.cpu,
            ram = client.ram,
            model = client.model
        )
    }

    override fun selectFirst(): Client {
        return clientQueries.selectFirst().executeAsOne()
    }

    override fun deleteAll() {
        clientQueries.deleteAll()
    }
}
