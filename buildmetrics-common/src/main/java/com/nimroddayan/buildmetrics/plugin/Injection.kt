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

package com.nimroddayan.buildmetrics.plugin

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import mu.KotlinLogging
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Dependency injection
 */
object Injection {
    /**
     * Instance of OkHttp
     */
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(HttpGradleLogger()).apply { level = HttpLoggingInterceptor.Level.BODY })
        .callTimeout(3L, TimeUnit.SECONDS)
        .build()

    /**
     * Instance of Moshi
     */
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    /**
     * Creates instance of Retrofit
     */
    fun retrofit(baseUrl: HttpUrl): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()
}

/**
 * OkHttp logger
 */
private class HttpGradleLogger : HttpLoggingInterceptor.Logger {
    private val log = KotlinLogging.logger {}

    override fun log(message: String) {
        log.debug(message)
    }
}
