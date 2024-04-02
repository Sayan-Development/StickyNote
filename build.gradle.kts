plugins {
    kotlin("jvm") version "1.9.22"
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    group = "org.sayandevelopment"
    version = "1.0.5"

    plugins.apply("java")
    plugins.apply("maven-publish")
    plugins.apply("kotlin")
    plugins.apply("com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
    }

    java {
        withJavadocJar()
        withSourcesJar()

        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

subprojects {
    tasks {
        build {
            dependsOn(shadowJar)
        }

        shadowJar {
            archiveFileName.set("${rootProject.name}-${version}-${this@subprojects.name}.jar")
            archiveClassifier.set("")
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
            exclude("META-INF/**")
            from("LICENSE")
            minimize()
        }

        jar {
            enabled = false
        }
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                pom {
                    name.set("stickynote")
                    description.set("A modular Kotlin library for Minecraft: JE")
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
        }

        repositories {
            maven {
                name = "sayandevelopment-repo"
                url = uri("https://repo.sayandevelopment.org/snapshots/")

                credentials {
                    username = project.findProperty("repo.sayan.user") as String
                    password = project.findProperty("repo.sayan.token") as String
                }
            }
        }
    }
}

