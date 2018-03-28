package com.github.blindpirate.junit.extension

import io.github.glytching.junit.extension.folder.TemporaryFolder
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File

@ExtendWith(TemporaryFolderExtension::class)
class SmokeTest {
    private var temporaryFolder: TemporaryFolder? = null

    @BeforeEach
    fun setUp(temporaryFolder: TemporaryFolder) {
        this.temporaryFolder = temporaryFolder
    }

    @ParameterizedTest
    @CsvSource("5.1.0, 1.2.31")
    fun `smoke test should succeed with JUnit Platform {0} and kotlin {1}`(jUnitVersion: String, kotlinVersion: String) {
        val buildFile = temporaryFolder!!.createFile("build.gradle.kts")
        val rootDir = buildFile.parentFile
        buildFile.writeText("""
           import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.gradle.kotlin.dsl.repositories

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    testCompile("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testCompile("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testCompile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    testCompile("org.junit.jupiter:junit-jupiter-params:$jUnitVersion")
    testCompile(files("${System.getProperty("unroll.extension.lib.jar.path")}"))
    testCompile("io.github.glytching:junit-extensions:1.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
}

tasks.withType<Test>().all {
    useJUnitPlatform()
}
        """.trimIndent())


        temporaryFolder!!.createDirectory("src/test/kotlin/foo")
        temporaryFolder!!.createFile("src/test/kotlin/foo/Math.kt").writeText("""
            package foo

            import java.lang.Math
            import com.github.blindpirate.Param
            import com.github.blindpirate.junit.extension.where
            import com.github.blindpirate.junit.extension.Unroll

            class Math {
                @Unroll
                fun `max number of {0} and {1} is {2}`(
                    a: Int, b: Int, c: Int, param: Param = where {
                        1 _ 3 _ 3
                        7 _ 4 _ 7
                        0 _ 0 _ 0
                }) {
                    assert(Math.max(a, b) == c)
                }
            }
            """)

        val result = GradleRunner.create()
                .withProjectDir(rootDir)
                .withArguments("test", "-u", "--console=plain")
                .build()

        print(result.output)

        assertTrue(result.task(":test")?.outcome == TaskOutcome.SUCCESS)

        val html = File(rootDir, "build/reports/tests/test/classes/foo.Math.html").readText()
        assertTrue(html.contains("""
            <div class="infoBox" id="tests">
            <div class="counter">3</div>
            <p>tests</p>
        """.trimIndent()))
        assertTrue(html.contains("max number of 1 and 3 is 3"))
        assertTrue(html.contains("max number of 7 and 4 is 7"))
        assertTrue(html.contains("max number of 0 and 0 is 0"))
    }
}