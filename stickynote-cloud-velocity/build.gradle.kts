dependencies {
    api(libs.cloud.velocity)

    api(project(":stickynote-cloud"))

    compileOnly(libs.velocity)
    compileOnly(project(":stickynote-proxy:stickynote-proxy-velocity"))
}
