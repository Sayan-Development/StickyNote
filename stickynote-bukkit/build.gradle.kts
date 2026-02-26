dependencies {
    api(libs.libby.paper)
    api(libs.inventoryframework)
    api(libs.xseries) { isTransitive = false }
    api(libs.pathetic.pathfinder.bukkit)
    api(libs.mccoroutines.bukkit.api)
    api(libs.mccoroutines.bukkit.core)
    api(libs.mccoroutines.folia.api)
    api(libs.mccoroutines.folia.core)
    api(libs.adventure.platform.bukkit)

    compileOnlyApi(libs.authlib)
    compileOnlyApi(libs.placeholderapi)
    compileOnlyApi(libs.skinsrestorer.api)

    compileOnly(libs.paper)
    compileOnly(libs.folia)

    compileOnly(project(":stickynote-core"))
}
