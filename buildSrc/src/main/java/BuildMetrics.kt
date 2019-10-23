import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.GradleInternal
import org.gradle.api.invocation.Gradle
import org.gradle.internal.scan.time.BuildScanBuildStartedTime
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

private val PLAIN_TEXT = "plain/text".toMediaType()

class BuildMetricsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension =
            project.extensions.create("buildMetrics", BuildMetricsExtension::class.java, project)
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(3L, TimeUnit.SECONDS)
            .build()
        project.gradle.addBuildListener(
            BuildDurationTracker(
                extension,
                User("555", "Nimrod"),
                okHttpClient,
                project.gradle.startParameter.isOffline
            )
        )
    }
}

open class BuildMetricsExtension(project: Project) {
    var trackingId: String = ""
}

data class User(
    val uid: String,
    val name: String
)

data class Event(
    val trackingId: String,
    val uid: String,
    val category: String,
    val action: String,
    val label: String,
    val value: String
) {
    fun toRequestBody(): RequestBody {
        return "v=1&tid=$trackingId&uid=$uid&t=event&ec=$category&ea=$action&el=$label&ev=$value"
            .toRequestBody(PLAIN_TEXT)
    }
}

class BuildDurationTracker(
    private val extension: BuildMetricsExtension,
    private val user: User,
    private val httpClient: OkHttpClient,
    private val isOffline: Boolean,
    private val url: HttpUrl = HttpUrl.Builder().scheme("https").host("www.google-analytics.com").addPathSegment("collect").build()
) : BuildListener {
    private val log = LoggerFactory.getLogger(BuildDurationTracker::class.java)
    private var buildStart: Long = 0

    override fun settingsEvaluated(gradle: Settings) {
    }

    override fun buildFinished(buildResult: BuildResult) {
        val duration = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - buildStart)
        println("Build start: $buildStart took $duration")
        trackBuildFinished(buildResult.failure == null, duration)
    }

    override fun projectsLoaded(gradle: Gradle) {
    }

    override fun buildStarted(gradle: Gradle) {
    }

    override fun projectsEvaluated(gradle: Gradle) {
        buildStart = (gradle as GradleInternal)
            .services.get(BuildScanBuildStartedTime::class.java)
            ?.buildStartedTime ?: System.currentTimeMillis()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun trackBuildFinished(isSuccessful: Boolean, buildDuration: Long) {
        val event = Event(
            trackingId = extension.trackingId,
            uid = user.uid,
            category = "Build",
            action = "Finished",
            label = if (isSuccessful) "Success" else "Failure",
            value = "$buildDuration"
        )
        if (isOffline) {
            cacheTracked(event)
        } else {
            postEvent(event)
        }
    }

    private fun postEvent(event: Event) {
        val request = Request.Builder()
            .url(url)
            .post(event.toRequestBody())
            .build()

        try {
            println("Tracking analytics $event")
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    cacheTracked(event)
                } else {
                    println("Request successful")
                }
            }
        } catch (e: Exception) {
            cacheTracked(event)
        }
    }

    private fun cacheTracked(event: Event) {
        // TODO store in local DB
    }
}

// READ https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide
// Ref https://github.com/cdsap/Talaiot
