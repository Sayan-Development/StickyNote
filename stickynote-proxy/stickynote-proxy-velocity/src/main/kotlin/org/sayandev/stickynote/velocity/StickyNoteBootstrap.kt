package org.sayandev.stickynote.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path

object StickyNoteBootstrap {

    @JvmStatic
    fun initialize(
        instance: Any,
        id: String,
        server: ProxyServer,
        logger: Logger,
        dataDirectory: Path,
        suspendingPluginContainer: SuspendingPluginContainer? = null
    ) {
        if (isLoaderMode(instance.javaClass.classLoader)) {
            initializeWithLoader(instance, id, server, logger, dataDirectory, suspendingPluginContainer)
            return
        }

        WrappedStickyNotePlugin(instance, id, server, logger, dataDirectory, suspendingPluginContainer).initialize()
    }

    private fun initializeWithLoader(
        instance: Any,
        id: String,
        server: ProxyServer,
        logger: Logger,
        dataDirectory: Path,
        suspendingPluginContainer: SuspendingPluginContainer?
    ) {
        val loaderClass = Class.forName(
            "org.sayandev.stickynote.loader.velocity.StickyNoteVelocityLoader",
            true,
            instance.javaClass.classLoader
        )

        val constructorWithCoroutine = runCatching {
            loaderClass.getConstructor(
                Any::class.java,
                String::class.java,
                ProxyServer::class.java,
                Logger::class.java,
                Path::class.java,
                SuspendingPluginContainer::class.java
            )
        }.getOrNull()

        if (constructorWithCoroutine != null) {
            constructorWithCoroutine.newInstance(instance, id, server, logger, dataDirectory, suspendingPluginContainer)
            return
        }

        val constructor = loaderClass.getConstructor(
            Any::class.java,
            String::class.java,
            ProxyServer::class.java,
            Logger::class.java,
            Path::class.java
        )
        constructor.newInstance(instance, id, server, logger, dataDirectory)
    }

    private fun isLoaderMode(classLoader: ClassLoader): Boolean {
        return runCatching {
            val stickyNotesClass = Class.forName(
                "org.sayandev.stickynote.generated.StickyNotes",
                false,
                classLoader
            )
            stickyNotesClass.declaredFields.any { field ->
                field.name.startsWith("DEPENDENCY_")
            }
        }.getOrElse {
            false
        }
    }
}
