package org.sayandev.stickynote.bukkit.hook

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer
import org.sayandev.stickynote.bukkit.hasPlugin
import org.sayandev.stickynote.bukkit.plugin

object PlaceholderAPIHook {

    var injectComponent: Boolean = false
    var sendWarningIfNotInstalled: Boolean = false

    fun injectComponent() {
        this.injectComponent = true
    }

    fun injectComponent(injectComponent: Boolean) {
        this.injectComponent = injectComponent
    }

    fun sendWarningIfNotInstalled() {
        this.sendWarningIfNotInstalled = true
    }

    fun sendWarningIfNotInstalled(sendWarningIfNotInstalled: Boolean) {
        this.sendWarningIfNotInstalled = sendWarningIfNotInstalled
    }

    fun injectPlaceholders(player: OfflinePlayer?, content: String): String {
        var finalContent = content
        if (injectComponent) {
            if (sendWarningIfNotInstalled && !hasPlugin("PlaceholderAPI")) {
                plugin.logger.warning("tried to parse placeholder for message `${content}` but PlaceholderAPI is not installed.")
            } else {
                finalContent = PlaceholderAPI.setPlaceholders(player, finalContent)
            }
        }
        return finalContent
    }

}