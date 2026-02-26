dependencies {
    api(libs.commandapi.paper.shade)
    api(libs.kotlinx.coroutines)

    api(project(":stickynote-command"))

    compileOnly(libs.paper)
    compileOnly(libs.folia)
    compileOnly(project(":stickynote-bukkit"))
}
