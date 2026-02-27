package org.sayandev.stickynote.paper

import org.bukkit.plugin.java.JavaPlugin

lateinit var wrappedPlugin: WrappedStickyNotePlugin

class WrappedStickyNotePlugin(val main: JavaPlugin, val exclusiveThreads: Int) {

    constructor(main: JavaPlugin) : this(main, 1)

    init {
        wrappedPlugin = this

        /*main.logger.info("Trying to download required libraries, make sure your machine is connected to internet.")
        val libraryManager = BukkitLibraryManager(main)

        val config = YamlConfiguration.loadConfiguration(InputStreamReader(this.main.getResource("repositories.yml")))
        libraryManager.addMavenLocal()
        libraryManager.addMavenCentral()
        config.getStringList("repositories").forEach {
            libraryManager.addRepository(it)
            main.logger.info("added `${it}` repository")
        }

        config.getStringList("dependencies").forEach { dependencyText ->
            main.logger.info("added `${dependencyText}` dependency")
            val splitted = dependencyText.split(":")
            val groupId = splitted[0]
            val artifactId = splitted[1]
            val version = splitted[2]
            val relocateFrom = splitted.getOrNull(3)
            val relocateTo = splitted.getOrNull(4)
            libraryManager.loadLibrary(
                Library.builder()
                    .groupId(groupId)
                    .artifactId(artifactId)
                    .version(version)
                    .apply {
                        if (relocateFrom != null && relocateTo != null) {
                            this.relocate(relocateFrom, relocateTo)
                        }
                    }
                    .build()
            )
        }*/
    }

    companion object {
        @JvmStatic
        fun getPlugin(): WrappedStickyNotePlugin {
            return wrappedPlugin
        }
    }
}