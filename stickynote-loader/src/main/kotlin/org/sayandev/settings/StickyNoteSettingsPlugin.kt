package org.sayandev.settings

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.maven

class StickyNoteSettingsPlugin : Plugin<Settings> {

    companion object {
        const val USE_SUBMODULE_PROPERTY = "stickynote.useSubmodule"
        const val SUBMODULE_PATH_PROPERTY = "stickynote.submodulePath"
    }

    override fun apply(settings: Settings) {
        val pluginVersion = this::class.java.`package`.implementationVersion
            ?: throw IllegalStateException("Cannot determine the plugin version")

        val useSubmodule = settings.providers.gradleProperty(USE_SUBMODULE_PROPERTY).orNull?.toBoolean() == true
        val submodulePath = settings.providers.gradleProperty(SUBMODULE_PATH_PROPERTY).orNull ?: "stickynote"

        if (useSubmodule) {
            val submoduleDir = settings.settingsDir.resolve(submodulePath)
            require(submoduleDir.isDirectory) {
                "StickyNote submodule mode is enabled, but '${submoduleDir.path}' was not found."
            }

            settings.includeBuild(submoduleDir)
        }

        settings.dependencyResolutionManagement {
            repositories {
                mavenLocal()
                maven("https://repo.sayandev.org/snapshots")
            }

            versionCatalogs {
                create("stickyNoteLibs") {
                    from("org.sayandev:stickynote-catalog:${pluginVersion}")
                }
            }
        }
    }
}
