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
//        compileClasspath = compileClasspath.plus(project.java.sourceSets["main"].output)
//        compileClasspath = compileClasspath.plus(project.configurations.testCompile)
//        runtimeClasspath = output.plus(compileClasspath)
    }
}

val jUnitPlatformVersion = "5.1.0"
val kotlinVersion = "1.2.31"

configurations.forEach { println(it) }
dependencies {
    compile("org.junit.jupiter:junit-jupiter-api:$jUnitPlatformVersion")
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    testCompile("org.junit.jupiter:junit-jupiter-params:$jUnitPlatformVersion")
    testCompile("io.github.glytching:junit-extensions:1.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitPlatformVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    "integrationTestCompile"(gradleTestKit())
    "integrationTestCompile"(java.sourceSets["main"].output)
    "integrationTestCompile"(java.sourceSets["test"].output)
    "integrationTestCompile"(configurations.testCompile)
    "integrationTestRuntime"(configurations.testRuntime)
}

tasks.create("integrationTest", Test::class.java) {
    testClassesDirs = java.sourceSets["integrationTest"].output.classesDirs
    classpath = java.sourceSets["integrationTest"].runtimeClasspath
}


tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test>().all {
    useJUnitPlatform()
}

