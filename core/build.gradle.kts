group = "org.sayandevelopment.core"

dependencies {
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks["jar"])
        }
    }

    repositories {
        maven {
            name = "jitpack"
            url = uri("https://jitpack.io/")
            content {
                includeGroup("com.github.Mohamad82Bz")
//                includeGroup("org.sayandevelopment.stickynote")
            }
        }
    }
}
