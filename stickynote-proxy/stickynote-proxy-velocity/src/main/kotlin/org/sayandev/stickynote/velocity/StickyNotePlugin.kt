package org.sayandev.stickynote.velocity

import com.velocitypowered.api.proxy.ProxyServer
import java.nio.file.Path
import java.util.logging.Logger

val plugin = wrappedPlugin

abstract class StickyNotePlugin(id: String, server: ProxyServer, logger: Logger, dataDirectory: Path, exclusiveThreads: Int) : WrappedStickyNotePlugin(id, server, logger, dataDirectory, exclusiveThreads) {
    constructor(id: String, server: ProxyServer, logger: Logger, dataDirectory: Path) : this(id, server, logger, dataDirectory, 1)
}