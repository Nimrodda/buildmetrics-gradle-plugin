package com.nimroddayan.buildmetrics.plugin

import com.nimroddayan.buildmetrics.publisher.mixpanel.ANALYTICS_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object Injection {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(HttpGradleLogger()).apply { level = HttpLoggingInterceptor.Level.BODY })
        .callTimeout(3L, TimeUnit.SECONDS)
        .build()

    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ANALYTICS_URL)
        .client(okHttpClient)
        .build()
}

private class HttpGradleLogger : HttpLoggingInterceptor.Logger {
    private val log = KotlinLogging.logger {}

    override fun log(message: String) {
        log.debug(message)
    }
}
