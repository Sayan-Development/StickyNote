dependencies {
    compileOnly(libs.velocity)
    compileOnlyApi(libs.libby.velocity)

    api(libs.mccoroutines.velocity.api)
    api(libs.mccoroutines.velocity.core)

    compileOnly(project(":stickynote-proxy"))
}