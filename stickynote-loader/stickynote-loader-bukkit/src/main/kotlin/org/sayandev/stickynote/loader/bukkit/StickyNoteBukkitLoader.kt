package org.sayandev.stickynote.loader.bukkit

import com.alessiodp.libby.BukkitLibraryManager
import com.alessiodp.libby.Library
import org.bukkit.plugin.java.JavaPlugin
import org.sayandev.stickynote.bukkit.WrappedStickyNotePlugin

object StickyNoteBukkitLoader {

    @JvmStatic
    fun load(plugin: JavaPlugin) {
        load(plugin, false)
    }

    @JvmStatic
    fun load(plugin: JavaPlugin, usePaperLoader: Boolean) {
        val stickyNotes = Class.forName("org.sayandev.stickynote.generated.StickyNotes")
        val dependencies = stickyNotes.fields.filter { it.name.startsWith("DEPENDENCY_") }.map { it.get(null) }
        val repositories = stickyNotes.fields.filter { it.name.startsWith("REPOSITORY_") }.map { it.get(null) } as List<String>

        // TODO: utilize paper library manager
        val libraryManager = if (usePaperLoader) BukkitLibraryManager(plugin) else BukkitLibraryManager(plugin)
        libraryManager.addMavenLocal()
        libraryManager.addRepository("https://repo.sayandev.org/snapshots")
        for (repository in repositories) {
            libraryManager.addRepository(repository.replace("{}", "."))
        }

        for (dependency in dependencies) {
            val group = (dependency::class.java.getMethod("getGroup").invoke(dependency) as String).replace(".", "{}")
            libraryManager.loadLibrary(
                Library.builder()
                    .groupId(group)
                    .artifactId(dependency::class.java.getMethod("getName").invoke(dependency) as String)
                    .version(dependency::class.java.getMethod("getVersion").invoke(dependency) as String)
                    /*.apply {
                        if (relocationFrom?.isNotEmpty() == true && relocationTo?.isNotEmpty() == true) {
                            this.relocate(group, relocationTo)
                        }
                    }*/
                    .build()
            )
        }

        WrappedStickyNotePlugin(plugin)
    }

}

