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
        clientQueries.insert(id = client.id)
    }

    override fun selectFirst(): Client {
        return Client.Impl(id = clientQueries.selectFirst().executeAsOne())
    }

    override fun deleteAll() {
        clientQueries.deleteAll()
    }
}
