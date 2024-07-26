dependencies {
    compileOnly(libs.velocity)

    api(libs.libby.velocity)

    compileOnly(project(":stickynote-proxy"))
    compileOnly(project(":stickynote-proxy:stickynote-proxy-velocity"))
}