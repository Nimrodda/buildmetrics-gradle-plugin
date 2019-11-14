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

package com.nimroddayan.buildmetrics

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class PluginTest {
    @get:Rule
    val testProjectDir = TemporaryFolder()

    private lateinit var buildFile: File

    @Before
    fun setup() {
        testProjectDir.newFile("settings.gradle").writeText("rootProject.name = 'test'")
        buildFile = testProjectDir.newFile("build.gradle")

    }

    @Test
    fun `run build successfully track`() {
        buildFile.writeText(
            """
            plugins {
                id "com.nimroddayan.buildmetrics.runtime"
            }
        """
        )

        runGradle()
    }

    @Test
    fun `apply plugin successfully`() {
        val project = ProjectBuilder.builder().build()

        project.pluginManager.apply("com.nimroddayan.buildmetrics.runtime")
    }

    private fun runGradle(): BuildResult {
        return GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments("tasks")
            .withPluginClasspath()
            .build()
    }
}
