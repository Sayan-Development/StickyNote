group = "org.sayandevelopment.core"

dependencies {
//    implementation("org.spongepowered:configurate-yaml:4.1.2")
//    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks["jar"])
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
