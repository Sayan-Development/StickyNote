dependencies {
    compileOnly(libs.velocity)

    api(libs.libby.velocity)
    api(libs.mccoroutines.velocity.api)
    api(libs.mccoroutines.velocity.core)
    api(libs.sqlite.jdbc)

    compileOnly(project(":stickynote-proxy"))
    compileOnly(project(":stickynote-proxy:stickynote-proxy-velocity"))

    api(project(":stickynote-loader:stickynote-loader-common"))
}