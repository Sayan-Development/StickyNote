package org.sayandev.stickynote.velocity

import com.google.inject.Inject
import com.velocitypowered.api.proxy.ProxyServer
import java.nio.file.Path
import java.util.logging.Logger

val plugin = wrappedPlugin

abstract class StickyNotePlugin @Inject constructor(instance: Any, id: String, server: ProxyServer, logger: Logger, dataDirectory: Path, exclusiveThreads: Int) : WrappedStickyNotePlugin(instance, id, server, logger, dataDirectory, exclusiveThreads) {
    @Inject constructor(instance: Any, id: String, server: ProxyServer, logger: Logger, dataDirectory: Path) : this(instance, id, server, logger, dataDirectory, 1)
}