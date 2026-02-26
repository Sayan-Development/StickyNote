dependencies {
    api(libs.cloud.paper)
    api(libs.cloud.minecraft.extras)

    api(project(":stickynote-cloud"))

    compileOnly(libs.paper)
    compileOnly(libs.folia)
    compileOnly(project(":stickynote-bukkit"))
}
