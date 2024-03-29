pluginManagement {
    repositories {
        gradlePluginPortal()

        // Used for the gradle plugin
        maven("https://repo.screamingsandals.org/public")
    }
}

rootProject.name = "StickyNote"

include("core")
include("bukkit")
