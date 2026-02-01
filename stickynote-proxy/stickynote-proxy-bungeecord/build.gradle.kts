dependencies {
    compileOnly(libs.bungeecord)
    api(libs.mccoroutines.bungeecord.api)
    api(libs.mccoroutines.bungeecord.core)
    api(libs.adventure.platform.bungeecord)

    compileOnlyApi(libs.libby.bungee)

    compileOnly(project(":stickynote-core"))
    compileOnly(project(":stickynote-proxy"))
}