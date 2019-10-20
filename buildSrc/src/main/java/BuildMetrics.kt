import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.GradleInternal
import org.gradle.api.invocation.Gradle
import org.gradle.internal.scan.time.BuildScanBuildStartedTime
import java.util.concurrent.TimeUnit

class BuildMetricsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.gradle.addBuildListener(BuildDurationTracker())
    }
}

class BuildDurationTracker : BuildListener {

    private var buildStart: Long = 0

    override fun settingsEvaluated(gradle: Settings) {
    }

    override fun buildFinished(buildResult: BuildResult) {
        println("Build start: $buildStart took ${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - buildStart)}")
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
}

// READ https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide
// Ref https://github.com/cdsap/Talaiot
