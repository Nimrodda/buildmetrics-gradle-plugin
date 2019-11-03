package com.nimroddayan.buildmetrics.publisher

/**
 * Listener for build metric events
 */
interface BuildMetricsListener {
    /**
     * Event called when build finished
     */
    fun onBuildFinished(client: Client, event: BuildFinishedEvent)

    /**
     * Event called when a client was created
     *
     * This event is called only once per client unless the client deletes the database file
     */
    fun onClientCreated(client: Client)
}
