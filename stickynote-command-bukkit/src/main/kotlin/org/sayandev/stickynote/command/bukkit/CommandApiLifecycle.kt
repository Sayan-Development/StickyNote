package org.sayandev.stickynote.command.bukkit

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIPaperConfig
import org.bukkit.plugin.java.JavaPlugin

object CommandApiLifecycle {

    private var loadedPlugin: JavaPlugin? = null
    private var enabled = false

    @JvmStatic
    @Synchronized
    fun load(plugin: JavaPlugin, configure: (CommandAPIPaperConfig.() -> Unit)? = null) {
        if (loadedPlugin === plugin) {
            return
        }

        val config = CommandAPIPaperConfig(plugin)
            .silentLogs(true)

        configure?.invoke(config)

        CommandAPI.onLoad(config)
        loadedPlugin = plugin
        enabled = false
    }

    @JvmStatic
    @Synchronized
    fun enable() {
        if (loadedPlugin == null || enabled) {
            return
        }

        CommandAPI.onEnable()
        enabled = true
    }

    @JvmStatic
    @Synchronized
    fun disable() {
        if (loadedPlugin == null) {
            return
        }

        CommandAPI.onDisable()
        loadedPlugin = null
        enabled = false
    }
}
