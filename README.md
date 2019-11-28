# Build Metrics Gradle plugin

Build metrics Gradle plugin is a set of plugins that track any task execution's duration and other useful 
metrics, such as hardware specs, etc. The plugin allows ease of extension via simple API. You can develop your own
extension that will track build events to whatever service you wish. 

The plugin consists of several ready made extensions that can be combined together or be used separately. 
The base plugin stores all events to a local database. If any of the extension is used then the base plugin 
will attempt to first track events with the using the service provided by the extension 
and only if it fails to do so it will log them to the local database. 
In this case, the next build will attempt to upload 
the cached events so far and clear the cache once all events are uploaded.

## Supported Analytics services

* Amplitude
* Mixpanel
* Google Analytics

## What data is tracked?

Check out `Client` and `BuildFinishedEvent` data classes to see what data is tracked.

## How does it work?

The plugin hooks up to Gradle build lifecycle to figure out when the build started and when it has finished.
During the first run, a client info is created and stored in a local SQLite database named `.buildmetrics`, which is stored
in the root project folder. The reason it is stored there instead of build folder is because build folder is often cleaned and since
we want to keep the client info throughout the builds. Another reason is because events can also be stored in the local database.
You must not delete this file and also do not commit it to version control (Add it to your `.gitignore`).
The database file is unique per machine.

When build is finished, the plugin will upload the event and client info to the analytics service based on the 
plugin you applied or if you just applied the runtime plugin, it will store the event in the local database. 
Events are stored in the local database also when the request to the analytics service has failed. 
The plugin will then attempt to upload the cached events during the next time a build is finished.

## Download

Add the following to the top of your `settings.gradle` file:

```groovy
pluginManagement {
    repositories {
        // This repository is required for dependencies used by the plugin
        // namely com.nimroddadyan.buildmetrics:buildmetrics-db
        // and com.nimroddayan.buildmetrics:buildmetrics-common
        // The plan is to publishe these dependencies to jcenter so there won't be a need for this extra repo
        maven {
            url  "https://dl.bintray.com/nimroddayan/buildmetrics"
        }
        mavenCentral()
        jcenter()
        google()
        gradlePluginPortal()
    }
}
```

### Runtime plugin

>Note that you don't need to apply the runtime plugin if you apply one of the analytics services extension plugins since
those already apply the runtime plugin. Which means that `buildMetrics` extension block is available with all the plugins.
 
```Gradle
plugins {
    id "com.nimroddayan.buildmetrics" version "0.1.0"
}

buildMetrics {
    // Track only assemble and test tasks
    // Note that if you chain multiple tasks, e.g. when running `./gradlew assemble publish` 
    // tracking is decided based on the first task, which in this case is `assemble`. 
    taskFilter = ["assemble", "test"]
}

``` 

### Amplitude
 
```Gradle
plugins {
    id "com.nimroddayan.buildmetrics.amplitude" version "0.1.0"
}

amplitude {
    apiKey = "key"
}

``` 

### Mixpanel
 
```Gradle
plugins {
    id "com.nimroddayan.buildmetrics.mixpanel" version "0.1.0"
}

mixpanel {
    token = "token"
}

``` 

### Google Analytics
 
```Gradle
plugins {
    id "com.nimroddayan.buildmetrics.googleanalytics" version "0.1.0"
}

googleAnalytics {
    trackingId = "trackingId"
}
``` 

## Developing your own extension plugin

Developing your own extension is super easy. I highly recommend that you use one of the analytics services plugins 
in this repository as a reference, for example, `buildmetrics-amplitude`.

Your plugin needs to depend on `com.nimroddayan.buildmetrics:buildmetrics:$version`, which exposes an interface
`BuildMetricsListener` that you need to implement in your plugin and then in your plugin's `apply` function, 
register your implementation of `BuildMetricsListener` by getting `BuildMetricsExtensions`. You must first apply
`BuildMetricsPlugin` before you can get the extensions. 

For example:

```kotlin
class BuildMetricsAmplitudePlugin : Plugin<Project>, BuildMetricsListener {
    override fun onClientCreated(client: Client) {
        // This callback is called only once per client
    }

    override fun onBuildFinished(client: Client, event: BuildFinishedEvent) {
        // This callback is called when the build is finished
    }

    override fun apply(project: Project) {
        // First we apply buildmetrics plugin (this is the runtime plugin)
        project.pluginManager.apply(BuildMetricsPlugin::class.java)
        // Then we get the extensions and register our listener implementation
        project.extensions.getByType(BuildMetricsExtensions::class.java).register(this)
    }
}
``` 

In your `build.gradle`:

```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/nimroddayan/buildmetrics"
    }
    gradlePluginPortal()
}

dependencies {
    implementation "com.nimroddayan.buildmetrics:buildmetrics:$version"
}
```

## Contributing

Make a Pull Request.

## Reporting an issue

Use Github issue tracker.

## License

Copyright 2019 Nimrod Dayan nimroddayan.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
