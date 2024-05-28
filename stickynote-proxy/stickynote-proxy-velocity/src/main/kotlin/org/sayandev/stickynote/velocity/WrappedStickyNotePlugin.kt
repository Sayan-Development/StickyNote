package org.sayandev.stickynote.velocity

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import java.nio.file.Path
import java.util.logging.Logger

lateinit var wrappedPlugin: WrappedStickyNotePlugin

open class WrappedStickyNotePlugin @Inject constructor(
    val id: String,
    val server: ProxyServer,
    val logger: Logger,
    @DataDirectory val dataDirectory: Path,
    val exclusiveThreads: Int
) {
    @Inject constructor(id: String, server: ProxyServer, logger: Logger, @DataDirectory dataDirectory: Path) : this(id, server, logger, dataDirectory, 1)

    lateinit var container: PluginContainer

    init {
        wrappedPlugin = this
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        initialize()
    }

    fun initialize() {
        registerContainer()
        onInitialize()
    }

    open fun onInitialize() {
    }

    fun registerContainer() {
        container = server.pluginManager.getPlugin(id).get()
    }

    companion object {
        @JvmStatic
        fun getPlugin(): WrappedStickyNotePlugin {
            return wrappedPlugin
        }
    }
}