package org.sayandev.stickynote.bukkit.hook

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.OfflinePlayer
import org.sayandev.stickynote.bukkit.hasPlugin
import org.sayandev.stickynote.bukkit.plugin

object PlaceholderAPIHook {

    var injectComponent: Boolean = false
    var sendWarningIfNotInstalled: Boolean = false

    @JvmStatic
    fun injectComponent(): PlaceholderAPIHook {
        this.injectComponent = true
        return this
    }

    @JvmStatic
    fun injectComponent(injectComponent: Boolean): PlaceholderAPIHook {
        this.injectComponent = injectComponent
        return this
    }

    @JvmStatic
    fun sendWarningIfNotInstalled(): PlaceholderAPIHook {
        this.sendWarningIfNotInstalled = true
        return this
    }

    @JvmStatic
    fun sendWarningIfNotInstalled(sendWarningIfNotInstalled: Boolean): PlaceholderAPIHook {
        this.sendWarningIfNotInstalled = sendWarningIfNotInstalled
        return this
    }

    @JvmStatic
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