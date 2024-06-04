import java.util.*

plugins {
    `kotlin-dsl`
    `java-library`
    kotlin("jvm") version "2.0.0"
//    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
//    implementation("com.github.johnrengelman:shadow:8.1.1")
    implementation("io.github.goooler.shadow:shadow-gradle-plugin:8.1.7")
    implementation(gradleApi())
}

val properties = Properties().also { props ->
    project.projectDir.resolveSibling("gradle.properties").bufferedReader().use {
        props.load(it)
    }
}