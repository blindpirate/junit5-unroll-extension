import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

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

    systemProperty("unroll.extension.lib.jar.path", "${project.buildDir.absolutePath}/libs/${project.name}.jar")
}


tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test>().all {
    useJUnitPlatform()
}


