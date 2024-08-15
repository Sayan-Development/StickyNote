import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
    version = "1.6.0"
    description = "A modular Kotlin library for Minecraft: JE"

    plugins.apply("maven-publish")
    plugins.apply("java-library")
    plugins.apply("kotlin")
    plugins.apply("com.github.johnrengelman.shadow")

    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
    }

    tasks {
        java {
            disableAutoTargetJvm()
        }
    }

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "sayandev"
            url = uri("https://repo.sayandev.org/snapshots")
        }
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
//            archiveClassifier.set("unshaded")
            enabled = true
        }

        afterEvaluate {
            val manualRelocations = mapOf(
                "com.github.patheloper.pathetic" to "patheloper",
                "com.github.cryptomorin" to "cryptomorin",
                "com.google.code.gson" to "gson",
            )
            val versionCatalogs = extensions.getByType(VersionCatalogsExtension::class.java)
            val libs = versionCatalogs.named("stickyNoteLibs")

            withType<ShadowJar> {
                for (bundleAlias in libs.bundleAliases) {
                    val bundle = libs.findBundle(bundleAlias).get().get()
                    for (alias in bundle) {
                        if (alias.module.name.contains("stickynote") || alias.module.name == "kotlin-stdlib" || alias.module.name == "kotlin-reflect") continue
                        if (manualRelocations.contains(alias.group)) {
                            val relocation = manualRelocations[alias.group]!!
                            relocate(relocation, "${project.group}.${slug}.libs.${relocation}")
                        } else {
                            relocate(alias.group, "${project.group}.${slug}.libs.${alias.group!!.split(".").lastOrNull()!!}")
                        }
                    }
                }
                mergeServiceFiles()
            }
        }

        val shadowJarNoDeps by creating(ShadowJar::class) {
            configurations = emptyList()
            from(sourceSets.main.get().output)
            archiveFileName.set("${rootProject.name}-${version}-${this@subprojects.name.removePrefix("stickynote-")}-no-dep.jar")
            archiveClassifier.set(null as String?)
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
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
                artifact(tasks["shadowJarNoDeps"])
                artifact(tasks["sourcesJar"])
                setPom(this)
            }
            create<MavenPublication>("maven-shaded") {
                groupId = rootProject.group as String
                artifactId += "-shaded"
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