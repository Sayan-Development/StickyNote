pluginManagement {
    repositories {
        gradlePluginPortal()

        // Takenaka
        maven("https://repo.screamingsandals.org/public")
    }
}

rootProject.name = "StickyNote"

include("stickynote-core")
include("stickynote-bukkit")
include("stickynote-paper")
