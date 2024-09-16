package org.sayandev.stickynote.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path

val plugin = wrappedPlugin

abstract class StickyNotePlugin @Inject constructor(instance: Any, id: String, server: ProxyServer, logger: Logger, dataDirectory: Path, suspendingPluginContainer: SuspendingPluginContainer, exclusiveThreads: Int) : WrappedStickyNotePlugin(instance, id, server, logger, dataDirectory, suspendingPluginContainer, exclusiveThreads) {
    @Inject constructor(instance: Any, id: String, server: ProxyServer, logger: Logger, dataDirectory: Path, suspendingPluginContainer: SuspendingPluginContainer) : this(instance, id, server, logger, dataDirectory, suspendingPluginContainer, 1)
}