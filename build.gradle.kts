import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    compile("org.junit.jupiter:junit-jupiter-api:5.1.0")
    compile("org.jetbrains.kotlin:kotlin-stdlib:1.2.31")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.1.0")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.2.31")
}


tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test>().all {
    useJUnitPlatform()
}

