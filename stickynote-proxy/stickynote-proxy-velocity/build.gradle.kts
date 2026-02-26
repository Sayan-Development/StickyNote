dependencies {
    compileOnly(libs.velocity)
    compileOnlyApi(libs.libby.velocity)
    api(libs.sqlite.jdbc)

    api(libs.mccoroutines.velocity.api)
    api(libs.mccoroutines.velocity.core)

    compileOnly(project(":stickynote-proxy"))
}
