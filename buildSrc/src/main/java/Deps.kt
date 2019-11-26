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

object Deps {
    private const val okhttpVersion = "4.2.2"
    private const val moshiVersion = "1.9.1"
    private const val retrofitVersion = "2.6.2"
    const val sqldelightVersion = "1.2.0"
    const val kotlinVersion = "1.3.60"
    
    const val okhttp = "com.squareup.okhttp3:okhttp:$okhttpVersion"
    const val okhttpLogging = "com.squareup.okhttp3:logging-interceptor:$okhttpVersion"
    const val junit = "junit:junit:4.12"
    const val mockito = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
    const val moshi = "com.squareup.moshi:moshi:$moshiVersion"
    const val moshiKotlin = "com.squareup.moshi:moshi-kotlin:$moshiVersion"
    const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
    const val retrofitMoshi = "com.squareup.retrofit2:converter-moshi:$retrofitVersion"
    const val kotlinLogging = "io.github.microutils:kotlin-logging:1.7.6"
    const val mockServer = "com.squareup.okhttp3:mockwebserver:$okhttpVersion"
    const val truth = "com.google.truth:truth:1.0"
    const val sqldelight = "com.squareup.sqldelight:sqlite-driver:$sqldelightVersion"
    const val oshi = "com.github.oshi:oshi-core:4.1.1"
    const val commonsIo = "commons-io:commons-io:2.6"
    const val kotlinStd = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
}
