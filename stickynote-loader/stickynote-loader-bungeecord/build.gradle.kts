dependencies {
    compileOnly(libs.bungeecord)

    api(libs.libby.bungee)

    compileOnly(project(":stickynote-proxy"))
    compileOnly(project(":stickynote-proxy:stickynote-proxy-bungeecord"))
}