dependencies {
    api(libs.commandapi.velocity.shade)
    api(libs.kotlinx.coroutines)

    api(project(":stickynote-command"))

    compileOnly(libs.velocity)
    compileOnly(project(":stickynote-proxy:stickynote-proxy-velocity"))
}
