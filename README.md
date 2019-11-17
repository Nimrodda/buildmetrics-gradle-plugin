# Build Metrics Gradle plugin

Build metrics Gradle plugin is a set of plugins that track 'assemble' tasks execution's duration and other useful 
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

TODO

## Download

### Base plugin

You don't need to apply the base plugin if you apply one or more of the analytics services extension plugins.
 
```Gradle
plugins {
    id "com.nimroddayan.buildmetrics" version "1.0.0"
}

``` 

### Amplitude
 
```Gradle
plugins {
    id "com.nimroddayan.buildmetrics.amplitude" version "1.0.0"
}

amplitude {
    apiKey = "key"
}

``` 

### Mixpanel
 
```Gradle
plugins {
    id "com.nimroddayan.buildmetrics.mixpanel" version "1.0.0"
}

mixpanel {
    token = "token"
}

``` 

### Google Analytics
 
```Gradle
plugins {
    id "com.nimroddayan.buildmetrics.googleanalytics" version "1.0.0"
}

googleAnalytics {
    trackingId = "trackingId"
}
``` 

### Developing your own extension plugin

TODO


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
