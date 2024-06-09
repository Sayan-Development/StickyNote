package org.sayandev.stickynote.loader.bukkit

import com.alessiodp.libby.BukkitLibraryManager
import com.alessiodp.libby.Library
import org.bukkit.plugin.java.JavaPlugin
import org.sayandev.stickynote.bukkit.WrappedStickyNotePlugin

object StickyNoteBukkitLoader {

    @JvmStatic
    fun load(plugin: JavaPlugin) {
        val stickyNotes = Class.forName("org.sayandev.generated.StickyNotes")
        val useLoader = stickyNotes.getField("USE_LOADER").get(null) as? Boolean
        if (useLoader == true) {
            val dependencies = listOfNotNull(
                stickyNotes.fields.find { it.name == "STICKYNOTE_CORE" }?.get(null),
                stickyNotes.fields.find { it.name == "STICKYNOTE_BUKKIT" }?.get(null),
                stickyNotes.fields.find { it.name == "STICKYNOTE_BUKKIT_NMS" }?.get(null)
            )

            val relocation = stickyNotes.getField("RELOCATION").get(null)
            val relocationFrom = relocation::class.java.getMethod("getFrom").invoke(relocation) as? String
            val relocationTo = relocation::class.java.getMethod("getTo").invoke(relocation) as? String

            val libraryManager = BukkitLibraryManager(plugin)
            libraryManager.addMavenLocal()
            libraryManager.addRepository("https://repo.sayandev.org/snapshots")

            for (dependency in dependencies) {
                libraryManager.loadLibrary(
                    Library.builder()
                        .groupId((dependency::class.java.getMethod("getGroup").invoke(dependency) as String).replace(".", "{}"))
                        .artifactId(dependency::class.java.getMethod("getName").invoke(dependency) as String)
                        .version(dependency::class.java.getMethod("getVersion").invoke(dependency) as String)
                        .apply {
                            plugin.logger.warning("trying to relocate from ${relocationFrom} to ${relocationTo}")
                            if (relocationFrom?.isNotEmpty() == true && relocationTo?.isNotEmpty() == true) {
                                this.relocate(relocationFrom, relocationTo)
                            }
                        }
                        .build()
                )
            }

            WrappedStickyNotePlugin(plugin)
        }
    }

}

