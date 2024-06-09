import org.sayandev.Module
import org.sayandev.applyDependencies
import org.sayandev.applyRepositories
import org.sayandev.applyShadowRelocation

plugins {
    `kotlin-dsl`
    publishing
    id("com.gradle.plugin-publish") version "1.2.1"
}

repositories {
    applyRepositories(Module.LOADER)
}

dependencies {
    compileOnly(gradleApi())
    implementation("io.github.goooler.shadow:shadow-gradle-plugin:8.1.7")

    applyDependencies(Module.LOADER)
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.LOADER)
    }
}

gradlePlugin {
    vcsUrl = "https://github.com/Sayan-Development/StickyNote.git"
    website = "https://sayandev.org"

    plugins {
        create("stickynote") {
            id = "org.sayandev.stickynote"
            displayName = "StickyNote"
            description = rootProject.description
            tags = listOf("minecraft", "paper", "spigot", "velocity")
            implementationClass = "org.sayandev.plugin.StickyNotePlugin"
        }
    }
}