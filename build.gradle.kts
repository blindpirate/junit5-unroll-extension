import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

val jUnitPlatformVersion = "5.1.0"
val kotlinVersion = "1.2.31"

dependencies {
    compile("org.junit.jupiter:junit-jupiter-api:$jUnitPlatformVersion")
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    testCompile("org.junit.jupiter:junit-jupiter-params:$jUnitPlatformVersion")
    testCompile("io.github.glytching:junit-extensions:1.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitPlatformVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
}


tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test>().all {
    useJUnitPlatform()
}

