import org.sayandev.Module
import org.sayandev.applyDependencies
import org.sayandev.applyRepositories
import org.sayandev.applyShadowRelocation

plugins {
    `kotlin-dsl`
    publishing
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
    publishing {
        repositories {
            maven {
                name = "sayandevelopment-repo"
                url = uri("https://repo.sayandev.org/snapshots/")

                credentials {
                    username = System.getenv("REPO_SAYAN_USER") ?: project.findProperty("repo.sayan.user") as String
                    password = System.getenv("REPO_SAYAN_TOKEN") ?: project.findProperty("repo.sayan.token") as String
                }
            }
        }
    }

    shadowJar {
        applyShadowRelocation(Module.LOADER)
    }
}

gradlePlugin {
    plugins {
        create("stickynote") {
            id = "org.sayandev.stickynote"
            implementationClass = "org.sayandev.plugin.StickyNotePlugin"
        }
    }
}