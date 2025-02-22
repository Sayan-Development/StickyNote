plugins {
    kotlin("jvm") version "2.1.0"
    `version-catalog`
    `maven-publish`
}

allprojects {
    group = "org.sayandev"
    version = "1.8.9.95"
    description = "A modular Kotlin framework for Minecraft: JE"

    plugins.apply("maven-publish")
    plugins.apply("version-catalog")
    plugins.apply("java-library")
    plugins.apply("kotlin")

    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
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
            name = "sayandev"
            url = uri("https://repo.sayandev.org/releases")
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
        withJavadocJar()
    }

    tasks {
        jar {
//            archiveClassifier.set("unshaded")
            enabled = true
        }

        /*afterEvaluate {
            val versionCatalogs = extensions.getByType(VersionCatalogsExtension::class.java)
            val libs = versionCatalogs.named("stickyNoteLibs")

            val excludedRelocations = setOf(
                "stickynote",
                "kotlin-stdlib",
                "kotlin-reflect",
                "kotlin",
                "kotlin-stdlib-jdk8",
                "kotlinx",
                "kotlinx-coroutines"
            )

            tasks.withType<ShadowJar> {
//                if (this.name != "shadowJarNoDeps") return@withType
                for (bundleAlias in libs.bundleAliases) {
                    val bundle = libs.findBundle(bundleAlias).get().get()
                    for (alias in bundle) {
                        if (excludedRelocations.contains(alias.group)) continue
                        relocate(alias.group, "${project.group}.${slug}.libs.${alias.group!!.split(".").last()}")

                    }
                }
                mergeServiceFiles()
            }
        }*/

        /*val shadowJarNoDeps by creating(ShadowJar::class) shadowJar {
            configurations = emptyList()
            from(sourceSets.main.get().output)
            archiveFileName.set("${rootProject.name}-${version}-${this@subprojects.name.removePrefix("stickynote-")}-no-dep.jar")
            archiveClassifier.set(null as String?)
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
        }*/

        /*shadowJar {
            archiveFileName.set("${rootProject.name}-${version}-${this@subprojects.name.removePrefix("stickynote-")}.jar")
            archiveClassifier.set(null as String?)
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
        }*/
    }

    publishing {
        publications {
            if (!project.name.contains("loader") && !project.name.contains("catalog")) {
                create<MavenPublication>("mavenJava") {
                    groupId = rootProject.group as String
//                    artifact(tasks["sourcesJar"])
//                    artifact(tasks["jar"])
                    if (project.name.contains("catalog")) {
                        from(components["versionCatalog"])
                    } else {
                        from(components["java"])
                    }

                    setPom(this)
                }
                /*create<MavenPublication>("catalog") {
                    groupId = rootProject.group as String
                    artifactId += "-catalog"
//                    artifact(tasks["sourcesJar"])
//                    artifact(tasks["jar"])
                    from(components["versionCatalog"])

                    setPom(this)
                }*/
            }
            if (project.name.contains("catalog")) {
                create<MavenPublication>("catalog") {
                    groupId = rootProject.group as String
                    from(components["versionCatalog"])

                    setPom(this)
                }
            }
        }

        repositories {
            maven {
                name = "sayandevelopment-repo"
                url = uri("https://repo.sayandev.org/snapshots/")

                credentials {
                    username = System.getenv("REPO_SAYAN_USER") ?: project.findProperty("repo.sayan.user") as String?
                    password = System.getenv("REPO_SAYAN_TOKEN") ?: project.findProperty("repo.sayan.token") as String?
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