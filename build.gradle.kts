plugins {
    kotlin("jvm") version "1.9.22"
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    group = "org.sayandevelopment.stickynote"
    version = "1.0.6"

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
}

subprojects {
    tasks {
        build {
            dependsOn(shadowJar)
        }

        shadowJar {
            archiveFileName.set("${rootProject.name}-${version}-${this@subprojects.name}.jar")
            destinationDirectory.set(file(rootProject.projectDir.path + "/bin"))
            exclude("META-INF/**")
            from("LICENSE")
            minimize()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("shadowJar") {
            artifact(tasks["shadowJar"])
        }
    }

    repositories {
        maven {
            name = "sayandevelopment-repo"
            url = uri("https://repo.sayandevelopment.org/releases/")

            credentials {
                username = "syrent"
                password = System.getenv("REPO_TOKEN")
            }
        }
    }
}

