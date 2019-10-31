package com.nimroddayan.buildmetrics.cache

import com.nimroddayan.buildmetrics.ClientQueries
import com.nimroddayan.buildmetrics.publisher.Client

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
            os_name = client.osName,
            os_version = client.osVersion,
            cpu = client.cpu,
            ram = client.ram,
            model = client.model
        )
    }

    override fun selectFirst(): Client {
        return clientQueries.selectFirst { id, os_name, os_version, cpu, ram, model ->
            Client(
                id = id,
                osName = os_name,
                osVersion = os_version,
                cpu = cpu,
                ram = ram,
                model = model
            )
        }.executeAsOne()
    }

    override fun deleteAll() {
        clientQueries.deleteAll()
    }
}
