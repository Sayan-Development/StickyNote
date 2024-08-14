dependencies {
    compileOnly(libs.folia)

    api(libs.libby.bukkit)

    compileOnly(project(":stickynote-bukkit"))
}

tasks {
    java {
        disableAutoTargetJvm()
    }
}