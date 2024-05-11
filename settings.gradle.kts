pluginManagement {
    repositories {
        gradlePluginPortal()

        // Takenaka
        maven("https://repo.screamingsandals.org/public")
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "StickyNote"

include("stickynote-core")
include("stickynote-bukkit")
include("stickynote-paper")
include("stickynote-bukkit:stickynote-bukkit-nms")
