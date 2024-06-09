package org.sayandev.plugin

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*

class StickyNotePlugin : Plugin<Project> {

    @KotlinPoetJavaPoetPreview
    override fun apply(target: Project) {
        val config = target.extensions.create<StickyNoteLoaderExtension>("stickynote", target)

        val createStickyNoteLoader by target.tasks.creating(StickyNoteTask::class) {
            group = "stickynote"
            description = "stickynote configurations and setup task"

            this.modules.set(config.modules)
            this.outputDir.set(config.outputDirectory)
            this.loaderVersion.set(config.loaderVersion)
            this.relocation.set(config.relocation)
            this.useLoader.set(config.useLoader)
        }

        target.tasks.withType<JavaCompile> {
            dependsOn(createStickyNoteLoader)
        }

        /*target.tasks.withType<ShadowJar> {
            relocate(config.relocation.get().first, config.relocation.get().second) {
                exclude("org/sayandev/generated/StickyNotes.class")
            }
        }*/

        @Suppress("UNCHECKED_CAST")
        runCatching { Class.forName("org.jetbrains.kotlin.gradle.tasks.KotlinCompile") as Class<Task> }
            .onSuccess { klass ->
                target.tasks.withType(klass) {
                    dependsOn(createStickyNoteLoader)
                }
            }

        target.afterEvaluate {
            require(createStickyNoteLoader.loaderVersion.get() != "0.0.0") { "loaderVersion is not provided" }

            val defaultLocation = layout.buildDirectory.dir("stickynote/output").get().asFile

            if (config.outputDirectory.get().asFile == defaultLocation) {
                extensions.getByType<JavaPluginExtension>().sourceSets["main"].java.srcDir(defaultLocation)
            }

            if (config.modules.get().map { it.type }.contains(StickyNoteModules.BUKKIT)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-bukkit:${config.loaderVersion.get()}")
            }
            if (config.modules.get().map { it.type }.contains(StickyNoteModules.VELOCITY)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-velocity:${config.loaderVersion.get()}")
            }
            if (config.modules.get().map { it.type }.contains(StickyNoteModules.BUNGEECORD)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-bungeecord:${config.loaderVersion.get()}")
            }

            createStickyNoteLoader.run()
        }
    }
}