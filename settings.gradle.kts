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

include("stickynote-loader")
include("stickynote-loader:stickynote-loader-bukkit")

include("stickynote-core")

include("stickynote-bukkit")
include("stickynote-bukkit:stickynote-bukkit-nms")

include("stickynote-paper")

include("stickynote-proxy")
include("stickynote-proxy:stickynote-proxy-velocity")
include("stickynote-proxy:stickynote-proxy-bungeecord")
include("stickynote-loader:stickynote-loader-velocity")
include("stickynote-loader:stickynote-loader-bungeecord")
