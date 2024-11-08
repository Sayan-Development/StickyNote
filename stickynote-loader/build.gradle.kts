plugins {
    kotlin("jvm") version "2.0.0"
    `kotlin-dsl`
    publishing
    id("com.gradle.plugin-publish") version "1.2.1"
    id("com.gradleup.shadow") version "8.3.4"
}

dependencies {
    compileOnly(gradleApi())

    api(libs.kotlin.poet.kotlin)
    api(libs.kotlin.poet.java)

    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.4")
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

allprojects {
    plugins.apply("com.gradleup.shadow")

    tasks {
        shadowJar {
            archiveFileName.set("${rootProject.name}-${version}-${this@allprojects.name.removePrefix("stickynote-")}.jar")
            archiveClassifier.set(null as String?)
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
        }

        build {
            dependsOn(shadowJar)
        }
    }

    publishing {
        publications {
            if (project.name.contains("loader")) {
                create<MavenPublication>("maven") {
                    groupId = rootProject.group as String
                    shadow.component(this)
//                    artifact(tasks["sourcesJar"])
//                    artifact(tasks["shadowJar"])
                }
            }
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