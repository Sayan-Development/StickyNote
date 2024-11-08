pluginManagement {
    repositories {
        gradlePluginPortal()

        // Takenaka
        maven("https://repo.screamingsandals.org/public")
        maven("https://repo.screamingsandals.org/snapshots")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        register("stickyNoteLibs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "StickyNote"

include("stickynote-core")
include("stickynote-catalog")

include("stickynote-bukkit")
include("stickynote-bukkit:stickynote-bukkit-nms")

include("stickynote-proxy")
include("stickynote-proxy:stickynote-proxy-velocity")
include("stickynote-proxy:stickynote-proxy-bungeecord")

include("stickynote-loader")
include("stickynote-loader:stickynote-loader-common")
include("stickynote-loader:stickynote-loader-bukkit")
include("stickynote-loader:stickynote-loader-bungeecord")
include("stickynote-loader:stickynote-loader-velocity")
