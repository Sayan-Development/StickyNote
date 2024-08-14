dependencies {
    compileOnly(libs.folia)

    api(libs.libby.bukkit)
    api(libs.libby.paper)

    compileOnly(project(":stickynote-bukkit"))
}

tasks {
    java {
        disableAutoTargetJvm()
    }
}