package com.nimroddayan.buildmetrics.publisher

interface BuildMetricsListener {
    fun onBuildFinished(client: Client, event: BuildFinishedEvent)
}
