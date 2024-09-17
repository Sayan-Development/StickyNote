package org.sayandev.stickynote.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path

lateinit var wrappedPlugin: WrappedStickyNotePlugin
lateinit var mainInstance: Any

open class WrappedStickyNotePlugin @Inject constructor(
    val instance: Any,
    val id: String,
    val server: ProxyServer,
    val logger: Logger,
    @DataDirectory val dataDirectory: Path,
    val suspendingPluginContainer: SuspendingPluginContainer?,
    val exclusiveThreads: Int,
) {
    @Inject constructor(instance: Any, id: String, server: ProxyServer, logger: Logger, @DataDirectory dataDirectory: Path, suspendingPluginContainer: SuspendingPluginContainer?) : this(instance, id, server, logger, dataDirectory, suspendingPluginContainer, 1)
    @Inject constructor(instance: Any, id: String, server: ProxyServer, logger: Logger, @DataDirectory dataDirectory: Path) : this(instance, id, server, logger, dataDirectory, null, 1)

    lateinit var container: PluginContainer

    init {
        mainInstance = instance
        wrappedPlugin = this
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        initialize()
    }

    fun initialize() {
        registerContainer()
        onInitialize()
        suspendingPluginContainer?.initialize(this)
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