import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    `java-library`
    `maven-publish`
    id("io.github.goooler.shadow") version "8.1.7"
}

allprojects {
    group = "org.sayandev"
    version = "1.0.3"

    plugins.apply("java-library")
    plugins.apply("maven-publish")
    plugins.apply("kotlin")
    plugins.apply("io.github.goooler.shadow")

    repositories {
        mavenCentral()
    }
}

subprojects {
    java {
        withSourcesJar()
        withJavadocJar()
    }

    val relocations = mapOf(
        "org.spongepowered" to "org.sayandev.stickynote.lib.spongepowered",
        "com.zaxxer" to "org.sayandev.stickynote.lib.zaxxer",
        "org.slf4j" to "org.sayandev.stickynote.lib.slf4j",
        "org.reflections" to "org.sayandev.stickynote.lib.reflections",
        "org.jetbrains" to "org.sayandev.stickynote.lib.jetbrains",
        "net.kyori" to "org.sayandev.stickynote.lib.kyori",
    )

    tasks {
        jar {
            archiveClassifier.set("unshaded")
        }

        withType<ShadowJar> {
            archiveFileName.set("${rootProject.name}-${version}-${this@subprojects.name.removePrefix("stickynote-")}.jar")
            archiveClassifier.set(null as String?)
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
//            exclude("**/META-INF")
            relocations.forEach { from, to ->
                relocate(from, to)
            }
            from("LICENSE")
        }
    }

    tasks.named<Jar>("sourcesJar") {
        relocations.forEach { (from, to) ->
            val filePattern = Regex("(.*)${from.replace('.', '/')}((?:/|$).*)")
            val textPattern = Regex.fromLiteral(from)
            eachFile {
                filter {
                    it.replaceFirst(textPattern, to)
                }
                path = path.replaceFirst(filePattern, "$1${to.replace('.', '/')}$2")
            }
        }
    }

    configurations {
        "apiElements" {
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_API))
                attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
                attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.SHADOWED))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements.JAR))
                attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
            }
            outgoing.artifact(tasks.named("shadowJar"))
        }
        "runtimeElements" {
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_RUNTIME))
                attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.LIBRARY))
                attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.SHADOWED))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.objects.named(LibraryElements.JAR))
                attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
            }
            outgoing.artifact(tasks.named("shadowJar"))
        }
        "mainSourceElements" {
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage.JAVA_RUNTIME))
                attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category.DOCUMENTATION))
                attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling.SHADOWED))
                attribute(DocsType.DOCS_TYPE_ATTRIBUTE, project.objects.named(DocsType.SOURCES))
            }
            outgoing.artifact(tasks.named("sourcesJar"))
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
//                from(components["java"])
                shadow.component(this)
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