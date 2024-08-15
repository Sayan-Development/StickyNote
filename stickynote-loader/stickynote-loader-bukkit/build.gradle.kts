dependencies {
    compileOnly(libs.folia)

    api(libs.libby.bukkit)
    api(libs.libby.paper)

    api(project(":stickynote-loader:stickynote-loader-common"))

    compileOnly(project(":stickynote-bukkit"))
}

tasks {
    java {
        disableAutoTargetJvm()
    }
}