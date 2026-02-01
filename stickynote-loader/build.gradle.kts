plugins {
    kotlin("jvm") version "2.2.20"
    `kotlin-dsl`
    publishing
    id("com.gradle.plugin-publish") version "2.0.0"
    id("com.gradleup.shadow") version "9.3.0"
}

dependencies {
    compileOnly(gradleApi())

    api(libs.kotlin.poet.kotlin)
    api(libs.kotlin.poet.java)

    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.3.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.2.20")
    implementation("org.jetbrains.kotlin.plugin.serialization:org.jetbrains.kotlin.plugin.serialization.gradle.plugin:2.2.20")
    testImplementation(kotlin("test"))
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
    plugins.apply("org.jetbrains.kotlin.jvm")
    plugins.apply("org.jetbrains.kotlin.plugin.serialization")

    tasks {
        shadowJar {
            archiveFileName.set("${rootProject.name}-${version}-${this@allprojects.name.removePrefix("stickynote-")}.jar")
            archiveClassifier.set(null as String?)
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin-sticky"))
        }

        build {
            dependsOn(shadowJar)
        }
    }

    publishing {
        publications {
            if (project.name.contains("loader")) {
                create<MavenPublication>("maven") {
                    version = rootProject.version.toString()
                    groupId = rootProject.group as String
                    from(components["shadow"])
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