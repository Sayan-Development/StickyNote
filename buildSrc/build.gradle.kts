import java.util.*

plugins {
    `kotlin-dsl`
    java
    `maven-publish`
    kotlin("jvm") version "2.0.0"
//    id("io.github.goooler.shadow") version "8.1.7"
}

version = "1.0.1"

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
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