package org.sayandev.stickynote.loader.velocity

import com.alessiodp.libby.Library
import com.alessiodp.libby.VelocityLibraryManager
import com.velocitypowered.api.proxy.ProxyServer
import org.sayandev.stickynote.velocity.WrappedStickyNotePlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

object StickyNoteVelocityLoader {

    @JvmStatic
    fun load(plugin: Any, id: String, server: ProxyServer, logger: Logger, dataDirectory: Path) {
        val stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes")
        val useLoader = stickyNotes.getField("USE_LOADER").get(null) as? Boolean
        if (useLoader == true) {
            val dependencies = listOfNotNull(
                stickyNotes.fields.find { it.name == "STICKYNOTE_CORE" }?.get(null),
                stickyNotes.fields.find { it.name == "STICKYNOTE_PROXY_VELOCITY" }?.get(null),
            )

            val relocation = stickyNotes.getField("RELOCATION").get(null)
            val relocationFrom = relocation::class.java.getMethod("getFrom").invoke(relocation) as? String
            val relocationTo = relocation::class.java.getMethod("getTo").invoke(relocation) as? String
            val relocate = stickyNotes.getField("RELOCATE").get(null) as? Boolean

            val libraryManager = VelocityLibraryManager(plugin, logger, dataDirectory, server.pluginManager)
            libraryManager.addMavenLocal()
            libraryManager.addRepository("https://repo.sayandev.org/snapshots")

            for (dependency in dependencies) {
                libraryManager.loadLibrary(
                    Library.builder()
                        .groupId((dependency::class.java.getMethod("getGroup").invoke(dependency) as String).replace(".", "{}"))
                        .artifactId(dependency::class.java.getMethod("getName").invoke(dependency) as String)
                        .version(dependency::class.java.getMethod("getVersion").invoke(dependency) as String)
                        .apply {
                            if (relocationFrom?.isNotEmpty() == true && relocationTo?.isNotEmpty() == true && relocate == true) {
                                this.relocate(relocationFrom, relocationTo)
                            }
                        }
                        .build()
                )
            }

            WrappedStickyNotePlugin(plugin, id, server, logger, dataDirectory).initialize()
        }
    }

}

