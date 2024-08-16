package org.sayandev.settings

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.maven

class StickyNoteSettingsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {

        val pluginVersion = this::class.java.`package`.implementationVersion
            ?: throw IllegalStateException("Cannot determine the plugin version")

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