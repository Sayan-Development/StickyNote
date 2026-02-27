pluginManagement {
    repositories {
        gradlePluginPortal()

        // sayandev
        maven("https://repo.sayandev.org/releases")
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
include("stickynote-configuration-kotlinx")
include("stickynote-configuration-configurate")
include("stickynote-database")
include("stickynote-messaging-redis")
include("stickynote-messaging-websocket")
include("stickynote-catalog")
include("stickynote-cloud")
include("stickynote-cloud-paper")
include("stickynote-cloud-velocity")
include("stickynote-command")
include("stickynote-command-paper")
include("stickynote-command-velocity")

include("stickynote-paper")
include("stickynote-paper:stickynote-paper-nms")

include("stickynote-proxy")
include("stickynote-proxy:stickynote-proxy-velocity")
include("stickynote-proxy:stickynote-proxy-bungeecord")

include("stickynote-loader")
include("stickynote-loader:stickynote-loader-common")
include("stickynote-loader:stickynote-loader-paper")
include("stickynote-loader:stickynote-loader-bungeecord")
include("stickynote-loader:stickynote-loader-velocity")
