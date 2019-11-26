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

import com.nimroddayan.buildmetrics.ClientQueries
import com.nimroddayan.buildmetrics.publisher.Client

internal interface ClientDao {
    fun insert(client: Client)
    fun selectFirst(): Client
    fun deleteAll()
    fun markSynced()
}

internal class ClientDaoSqlite(
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
        return clientQueries.selectFirst { id, os_name, os_version, cpu, ram, model, synced ->
            Client(
                id = id,
                osName = os_name,
                osVersion = os_version,
                cpu = cpu,
                ram = ram,
                model = model,
                synced = synced
            )
        }.executeAsOne()
    }

    override fun deleteAll() {
        clientQueries.deleteAll()
    }

    override fun markSynced() {
        clientQueries.markSynced()
    }
}

internal class ClientDaoNoOp : ClientDao {
    lateinit var client: Client

    override fun insert(client: Client) {
    }

    override fun selectFirst(): Client {
        return client
    }

    override fun deleteAll() {

    }

    override fun markSynced() {
    }
}
