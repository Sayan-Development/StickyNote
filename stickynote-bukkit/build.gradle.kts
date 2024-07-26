dependencies {
    compileOnlyApi(libs.libby.bukkit)
    compileOnlyApi(libs.adventure.platform.bukkit)
    compileOnlyApi(libs.cloud.paper)
    compileOnlyApi(libs.cloud.minecraft.extras)
    compileOnlyApi(libs.inventoryframework)
    compileOnlyApi(libs.xseries) { isTransitive = false }
    compileOnlyApi(libs.pathetic.pathfinder.bukkit)
    compileOnlyApi(libs.authlib)
    compileOnlyApi(libs.placeholderapi)
    compileOnlyApi(libs.skinsrestorer.api)

    compileOnly(libs.paper)
    compileOnly(libs.folia)

    compileOnly(project(":stickynote-core"))
}