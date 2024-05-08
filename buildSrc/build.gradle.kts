import java.util.*

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.9.23"
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.github.johnrengelman:shadow:8.1.1")
}

val properties = Properties().also { props ->
    project.projectDir.resolveSibling("gradle.properties").bufferedReader().use {
        props.load(it)
    }
}

dependencies {
    implementation(gradleApi())
}