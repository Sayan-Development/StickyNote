dependencies {
    compileOnly(libs.bungeecord)

    compileOnlyApi(libs.libby.bungee)
    compileOnlyApi(libs.adventure.platform.bungeecord)

    compileOnly(project(":stickynote-core"))
    compileOnly(project(":stickynote-proxy"))
}