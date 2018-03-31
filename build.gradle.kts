import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.novoda.gradle.release.PublishExtension

plugins {
    `kotlin-dsl`
    id("jacoco")
}

buildscript {
    repositories { jcenter() }
    dependencies { classpath("com.novoda:bintray-release:0.8.1") }
}

apply(mapOf("plugin" to "com.novoda.bintray-release"))

repositories {
    mavenCentral()
    jcenter()
}

configurations.create("ktlint")

java.sourceSets {
    "integrationTest" {
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDirs("src/integration-test/kotlin")
        }
    }
}

val jUnitPlatformVersion = "5.1.0"
val kotlinVersion = "1.2.31"
val jUnitLauncherVersion = "1.1.0"

configurations.getByName("integrationTestCompile").extendsFrom(configurations.testCompile)
configurations.getByName("integrationTestRuntime").extendsFrom(configurations.testRuntime)

dependencies {
    "ktlint"("com.github.shyiko:ktlint:0.20.0")

    compile("org.junit.jupiter:junit-jupiter-api:$jUnitPlatformVersion")
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    testCompile("org.junit.jupiter:junit-jupiter-params:$jUnitPlatformVersion")
    testCompile("io.github.glytching:junit-extensions:1.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitPlatformVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    "integrationTestCompile"("org.junit.platform:junit-platform-launcher:$jUnitLauncherVersion")
    "integrationTestCompile"(gradleTestKit())
    "integrationTestCompile"(java.sourceSets["main"].output)
    "integrationTestCompile"(java.sourceSets["test"].output)
    "integrationTestRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:$jUnitPlatformVersion")
}

tasks.create("integrationTest", Test::class.java) {
    shouldRunAfter("test")
    dependsOn("jar")
    testClassesDirs = java.sourceSets["integrationTest"].output.classesDirs
    classpath = java.sourceSets["integrationTest"].runtimeClasspath

    exclude("**/resources/**")

    systemProperty("unroll.extension.lib.jar.path", "${project.buildDir.absolutePath}/libs/${project.name}.jar")
}

tasks.create("ktlint", JavaExec::class.java) {
    tasks.getByName("check").dependsOn(this)
    classpath = configurations.getByName("ktlint")
    main = "com.github.shyiko.ktlint.Main"
    args("src/main/**/*.kt")
}


tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test>().all {
    tasks.getByName("check").dependsOn(this)
    useJUnitPlatform()
}

tasks.create("jacoco", JacocoReport::class.java) {
    dependsOn("test")
    reports {
        html.isEnabled = true
    }

    classDirectories = files("$buildDir/classes/kotlin/main")
    sourceDirectories = files("src/main/kotlin")
    executionData = files("$buildDir/jacoco/test.exec")
}

configure<PublishExtension> {
    userOrg = "blindpirate"
    repoName = "com.github.blindpirate"
    groupId = "com.github.blindpirate"
    artifactId = "junit5-unroll-extension"
    publishVersion = "0.1"
    desc = "JUnit 5 Unroll Extension for Kotlin"
}
