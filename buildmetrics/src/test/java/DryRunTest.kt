package com.nimroddayan.buildmetrics

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DryRunTest {
    @get:Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var buildFile: File

    @Before
    fun setup() {
        testProjectDir.newFile("settings.gradle").writeText("rootProject.name = 'test'")
        buildFile = testProjectDir.newFile("build.gradle")

    }

    @Test(expected = UnexpectedBuildFailure::class)
    fun `missing tracking id in extension throws exception`() {
        buildFile.writeText(
            """
            plugins {
                id "com.nimroddayan.gradle.build.metrics"
            }
        """
        )

        runGradle()
    }

    private fun runGradle(): BuildResult {
        return GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks")
            .withPluginClasspath()
            .build()
    }
}
