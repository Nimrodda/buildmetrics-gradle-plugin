package com.nimroddayan.buildmetrics.plugin

import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object Injection {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(HttpGradleLogger()).apply { level = HttpLoggingInterceptor.Level.BODY })
        .callTimeout(3L, TimeUnit.SECONDS)
        .build()
}

private class HttpGradleLogger : HttpLoggingInterceptor.Logger {
    private val log = KotlinLogging.logger {}

    override fun log(message: String) {
        log.debug(message)
    }
}
