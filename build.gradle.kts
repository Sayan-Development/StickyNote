plugins {
    kotlin("jvm") version "1.9.22"
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    group = "org.sayandevelopment"
    version = "1.0.42-SNAPSHOT"

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
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        build {
            dependsOn(shadowJar)
        }

        shadowJar {
            archiveFileName.set("${rootProject.name}-${version}-${this@allprojects.name.removePrefix("stickynote-")}.jar")
            archiveClassifier.set("")
//            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
            exclude("META-INF/**")
            relocate("net.kyori", "org.sayandevelopment.stickynote.lib.net.kyori")
            relocate("kotlin", "org.sayandevelopment.stickynote.lib.kotlin")
            relocate("com.zaxxer", "org.sayandevelopment.stickynote.lib.com.zaxxer")
            relocate("org.spongepowered", "org.sayandevelopment.stickynote.lib.org.spongepowered")
            relocate("org.self4j", "org.sayandevelopment.stickynote.lib.org.self4j")
            relocate("org.reflections", "org.sayandevelopment.stickynote.lib.org.reflections")
            relocate("org.jetbrains", "org.sayandevelopment.stickynote.lib.org.jetbrains")
            relocate("org.incendo", "org.sayandevelopment.stickynote.lib.org.incendo")
            from("LICENSE")
            minimize()
        }

        jar {
            enabled = false
        }
    }

    publishing {
        publications {
            create<MavenPublication>("shadow") {
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
                    username = System.getenv("REPO_SAYAN_USER") ?: project.findProperty("repo.sayan.user") as String
                    password = System.getenv("REPO_SAYAN_TOKEN") ?: project.findProperty("repo.sayan.token") as String
                }
            }
        }
    }
}

