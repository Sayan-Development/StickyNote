plugins {
    kotlin("jvm") version "2.0.0"
    `version-catalog`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val slug = "stickynote"

catalog {
    versionCatalog {
        from(files("${rootProject.projectDir}/gradle/libs.versions.toml"))
    }
}

publishing {
    publications {
        create<MavenPublication>("catalog") {
            artifactId = ("${artifactId.lowercase()}-catalog")
            from(components["versionCatalog"])
            setPom(this)
        }
    }
}

allprojects {
    group = "org.sayandev"
    version = "1.3.4"
    description = "A modular Kotlin library for Minecraft: JE"

    plugins.apply("maven-publish")
    plugins.apply("java-library")
    plugins.apply("kotlin")
    plugins.apply("com.github.johnrengelman.shadow")

    tasks {
        java {
            disableAutoTargetJvm()
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "extendedclip"
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        }
        maven {
            name = "spongepowered"
            url = uri("https://repo.spongepowered.org/maven/")
        }
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven {
            name = "spigotmc"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }
        maven {
            name = "sonatype-snapshots"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
        maven {
            name = "alessiodp"
            url = uri("https://repo.alessiodp.com/snapshots")
        }
        maven {
            name = "jitpack"
            url = uri("https://jitpack.io")
        }
        maven {
            name = "codemc"
            url = uri("https://repo.codemc.org/repository/maven-public/")
        }
    }
}

subprojects {
    java {
        withSourcesJar()
    }

    tasks {
        jar {
            archiveClassifier.set("unshaded")
            enabled = false
        }

        build {
            dependsOn(shadowJar)
        }

        shadowJar {
            archiveFileName.set("${rootProject.name}-${version}-${this@subprojects.name.removePrefix("stickynote-")}.jar")
            archiveClassifier.set(null as String?)
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group as String
                shadow.component(this)
                artifact(tasks["sourcesJar"])
                setPom(this)
            }
        }

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
}

fun setPom(publication: MavenPublication) {
    publication.pom {
        name.set("stickynote")
        description.set(rootProject.description)
        url.set("https://github.com/sayan-development/stickynote")
        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://github.com/sayan-development/stickynote/blob/master/LICENSE")
            }
        }
        developers {
            developer {
                id.set("mohamad82")
                name.set("mohamad")
                email.set("")
            }
            developer {
                id.set("syrent")
                name.set("abbas")
                email.set("syrent2356@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:github.com/sayan-development/stickynote.git")
            developerConnection.set("scm:git:ssh://github.com/sayan-development/stickynote.git")
            url.set("https://github.com/sayan-development/stickynote/tree/master")
        }
    }
}