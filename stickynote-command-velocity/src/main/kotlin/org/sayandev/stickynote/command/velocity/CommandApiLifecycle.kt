package org.sayandev.stickynote.command.velocity

import com.velocitypowered.api.proxy.ProxyServer
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIVelocityConfig

object CommandApiLifecycle {

    private var loaded = false
    private var enabled = false

    @JvmStatic
    @Synchronized
    fun load(server: ProxyServer, plugin: Any, configure: (CommandAPIVelocityConfig.() -> Unit)? = null) {
        if (loaded) {
            return
        }

        val config = CommandAPIVelocityConfig(server, plugin)
            .silentLogs(true)

        configure?.invoke(config)

        CommandAPI.onLoad(config)
        loaded = true
        enabled = false
    }

    @JvmStatic
    @Synchronized
    fun enable() {
        if (!loaded || enabled) {
            return
        }

        CommandAPI.onEnable()
        enabled = true
    }

    @JvmStatic
    @Synchronized
    fun disable() {
        if (!loaded) {
            return
        }

        CommandAPI.onDisable()
        loaded = false
        enabled = false
    }
}
