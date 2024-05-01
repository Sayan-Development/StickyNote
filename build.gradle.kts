import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.22"
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    group = "org.sayandevelopment"
    version = "1.0.0"

    plugins.apply("java-library")
    plugins.apply("maven-publish")
    plugins.apply("kotlin")
    plugins.apply("com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
    }
}

subprojects {
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

        withType<ShadowJar> {
            archiveFileName.set("${rootProject.name}-${version}-${this@subprojects.name.removePrefix("stickynote-")}.jar")
            archiveClassifier.set("")
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
            exclude("META-INF/**")
            mergeServiceFiles()
            exclude("**/*.kotlin_metadata")
            exclude("**/*.kotlin_builtins")
            relocate("net.kyori", "org.sayandevelopment.stickynote.lib.kyori")
            relocate("com.zaxxer", "org.sayandevelopment.stickynote.lib.zaxxer")
            relocate("org.spongepowered", "org.sayandevelopment.stickynote.lib.spongepowered")
            relocate("org.slf4j", "org.sayandevelopment.stickynote.lib.slf4j")
            relocate("org.reflections", "org.sayandevelopment.stickynote.lib.reflections")
            relocate("org.jetbrains", "org.sayandevelopment.stickynote.lib.jetbrains")
            relocate("org.incendo", "org.sayandevelopment.stickynote.lib.incendo")
            from("LICENSE")
            minimize()
        }
    }

    val javaComponent = components["java"] as AdhocComponentWithVariants
    println(configurations.asMap.keys)
    javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
        skip()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(javaComponent)
                setPom(this)
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

fun setPom(publication: MavenPublication) {
    publication.pom {
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