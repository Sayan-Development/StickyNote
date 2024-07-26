package org.sayandev.plugin

enum class StickyNoteModules(override val artifact: String, override val project: String) : StickyNoteModule {
    BUKKIT("stickynote-bukkit", "stickynote-bukkit"),
    BUKKIT_NMS("stickynote-bukkit-nms", "${BUKKIT.project}:stickynote-bukkit-nms"),
    PAPER("stickynote-paper", "stickynote-paper"),
    PROXY("stickynote-proxy", "stickynote-proxy"),
    VELOCITY("stickynote-proxy-velocity", "${PROXY.project}:stickynote-velocity"),
    BUNGEECORD("stickynote-proxy-bungeecord", "${PROXY.project}:stickynote-bungeecord"),
    LOADER("stickynote-loader", "stickynote-loader"),
    BUKKIT_LOADER("stickynote-loader-bukkit", "${LOADER.project}:stickynote-loader-bukkit"),
    VELOCITY_LOADER("stickynote-loader-velocity", "${LOADER.project}:stickynote-loader-velocity"),
    BUNGEE_LOADER("stickynote-loader-bungee", "${LOADER.project}:stickynote-loader-bungee"),
    NONE("", "");
}