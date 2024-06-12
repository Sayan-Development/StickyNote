package org.sayandev.stickynote.loader.bungee

import com.alessiodp.libby.BungeeLibraryManager
import com.alessiodp.libby.Library
import net.md_5.bungee.api.plugin.Plugin
import org.sayandev.stickynote.bungeecord.WrappedStickyNotePlugin

object StickyNoteBungeeLoader {

    @JvmStatic
    fun load(plugin: Plugin) {
        val stickyNotes = Class.forName("org.sayandev.generated.StickyNotes")
        val useLoader = stickyNotes.getField("USE_LOADER").get(null) as? Boolean
        if (useLoader == true) {
            val dependencies = listOfNotNull(
                stickyNotes.fields.find { it.name == "STICKYNOTE_CORE" }?.get(null),
                stickyNotes.fields.find { it.name == "STICKYNOTE_PROXY_BUNGEECORD" }?.get(null),
            )

            val relocation = stickyNotes.getField("RELOCATION").get(null)
            val relocationFrom = relocation::class.java.getMethod("getFrom").invoke(relocation) as? String
            val relocationTo = relocation::class.java.getMethod("getTo").invoke(relocation) as? String
            val relocate = stickyNotes.getField("RELOCATE").get(null) as? Boolean

            val libraryManager = BungeeLibraryManager(plugin)
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

            WrappedStickyNotePlugin(plugin).initialize()
        }
    }

}

