package org.sayandev.loader.common

import org.sayandev.LightClassLoader
import org.sayandev.light.*
import org.sayandev.light.dependency.Version
import org.sayandev.light.dependency.dependencies.MavenDependency
import org.sayandev.light.repository.repositories.MavenRepository
import java.io.File
import java.lang.reflect.Field
import java.util.*
import java.util.logging.Logger

abstract class StickyNoteLightLoaderKt {
    protected abstract fun onComplete()

    var stickyNotes: Class<*> = Class.forName("org.sayandev.stickynote.generated.StickyNotes")
    var relocate: Boolean = stickyNotes.getField("RELOCATE").get(stickyNotes) as Boolean

    fun load(id: String, dataDirectory: File, logger: Logger, classLoader: LightClassLoader) {
        val libFolder: File = generateLibDirectory(dataDirectory)

        logger.info("downloading required light")
        RequiredLights(libFolder)
            .download()
            .load(classLoader)

        logger.info("initializing dependency manager...")
        val dependencyManager = DependencyManager(
            classLoader,
            AsyncDispatcher("main-light", 1),
            AsyncDispatcher("download-light", 10),
            AsyncDispatcher("load-light", 10),
            AsyncDispatcher("resolve-light", 10),
            libFolder,
            logger,
            false
        )

        try {
            val repositories = getRepositories(stickyNotes)
            logger.info("got ${repositories.size} repositories")
            val dependencies = getDependencies(stickyNotes)
            logger.info("got ${dependencies.size} dependencies")

            val relocation = stickyNotes.getField("RELOCATION").get(stickyNotes)
            val relocationFrom = relocation.javaClass.getMethod("getFrom").invoke(relocation) as String
            val relocationTo = relocation.javaClass.getMethod("getTo").invoke(relocation) as String

            for (repository in repositories) {
                dependencyManager.addRepository(MavenRepository(repository))
                logger.info("added $repository to repositories (current size: ${dependencyManager.repositories.size})")
            }

            for (dependency in dependencies) {
                val mavenDependency = MavenDependency(
                    dependency.getGroup().replace("{}", "."),
                    dependency.getName(),
                    Version(dependency.getVersion()),
                    false
                )
                dependencyManager.addDependency(mavenDependency)
                logger.info("added $mavenDependency to dependencies (current size: ${dependencyManager.dependencies.size})")
            }

            for (addedDependency in dependencyManager.dependencies) {
                if (addedDependency.artifact == "sqlite-jdbc") {
                    try {
                        Class.forName("org.sqlite.JDBC")
                        continue
                    } catch (_: Exception) { }
                }
                val name = addedDependency.artifact

                if (name.contains("packetevents")) {
                    relocations.put("io{}github{}retrooper", relocationTo + "{}lib{}packetevents")
                    continue
                }
                if (name.contains("adventure")) {
                    continue
                }
                if (name.contains("stickynote")) {
                    relocations.put(relocationFrom, relocationTo + "{}lib{}stickynote")
                }
                relocations.put("com.mysql", relocationTo + "{}lib{}mysql")
                relocations.put(addedDependency.group, "${relocationTo}{}lib{}${addedDependency.group.split("{}").last()}")
            }

            for (dependency in dependencyManager.dependencies) {
                for ((relocationFrom, relocationTo) in relocations) {
                    dependency.addRelocation(Relocation(relocationFrom, relocationTo))
                }
            }

            logger.info("initializing main dispatcher...")
            CoroutineUtils.launch(AsyncDispatcher("light-main", 1)) {
                logger.info("starting downloading ${dependencyManager.dependencies.size} dependencies...")
                dependencyManager.downloadAll().await()
                logger.info("downloaded ${dependencyManager.dependencies.size} dependencies")
                logger.info("starting loading ${dependencyManager.dependencies.size} dependencies")
                dependencyManager.loadAll().await()
                logger.info("loaded ${dependencyManager.dependencies.size} dependencies")
                logger.info("starting saving ${dependencyManager.dependencies.size} dependencies")
                dependencyManager.saveAll()
                logger.info("saved ${dependencyManager.dependencies.size} dependencies")
                onComplete()
            }
        } catch (e: Exception) {
            e.fillInStackTrace()
        }
    }


    private fun getDependencies(stickyNotes: Class<*>): MutableList<Dependency> {
        return Arrays.stream<Field>(stickyNotes.getFields())
            .filter { field: Field? -> field!!.getName().startsWith("DEPENDENCY_") }
            .map<Dependency?> { field: Field? ->
                try {
                    val dependencyObject = field!!.get(null)
                    val dependencyFieldClass: Class<*> = dependencyObject.javaClass
                    return@map Dependency(
                        dependencyFieldClass.getMethod("getGroup").invoke(dependencyObject) as String?,
                        dependencyFieldClass.getMethod("getName").invoke(dependencyObject) as String?,
                        dependencyFieldClass.getMethod("getVersion").invoke(dependencyObject) as String?,
                        dependencyFieldClass.getMethod("getRelocation").invoke(dependencyObject) as String?,
                        dependencyFieldClass.getMethod("isStickyLoad").invoke(dependencyObject) as Boolean
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@map null
                }
            }.toList()
    }

    private fun getRepositories(stickyNotes: Class<*>): MutableList<String> {
        return Arrays.stream<Field>(stickyNotes.getFields())
            .filter { field: Field? -> field!!.getName().startsWith("REPOSITORY_") }
            .map<String?> { field: Field? ->
                try {
                    return@map field!!.get(null) as String?
                } catch (e: IllegalAccessException) {
                    throw RuntimeException(e)
                }
            }.toList()
    }

    companion object {
        val relocations: MutableMap<String, String> = mutableMapOf<String, String>()

        private const val LIB_FOLDER = "lib"

        fun generateLibDirectory(root: File?): File {
            return File(File(root, "stickynote"), LIB_FOLDER)
        }
    }
}
