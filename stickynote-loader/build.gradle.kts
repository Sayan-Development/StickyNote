
plugins {
    kotlin("jvm") version "2.0.0"
    `kotlin-dsl`
    publishing
    id("com.gradle.plugin-publish") version "1.2.1"
}

dependencies {
    compileOnly(gradleApi())

    api(libs.kotlin.poet.kotlin)
    api(libs.kotlin.poet.java)

    implementation("io.github.goooler.shadow:shadow-gradle-plugin:8.1.8")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
}

tasks {
    withType<Jar> {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        }
    }
}

gradlePlugin {
    vcsUrl = "https://github.com/Sayan-Development/StickyNote.git"
    website = "https://sayandev.org"

    plugins {
        create("stickynote-project") {
            id = "org.sayandev.stickynote.project"
            displayName = "StickyNoteProjectPlugin"
            description = rootProject.description
            tags = listOf("minecraft", "paper", "spigot", "velocity")
            implementationClass = "org.sayandev.plugin.StickyNoteProjectPlugin"
        }
        create("stickynote-settings") {
            id = "org.sayandev.stickynote.settings"
            displayName = "StickyNoteSettingsPlugin"
            description = rootProject.description
            tags = listOf("minecraft", "paper", "spigot", "velocity")
            implementationClass = "org.sayandev.settings.StickyNoteSettingsPlugin"
        }
    }
}