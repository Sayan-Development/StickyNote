package org.sayandev.plugin

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*
import kotlin.jvm.java


class StickyNoteProjectPlugin : Plugin<Project> {

    @KotlinPoetJavaPoetPreview
    override fun apply(target: Project) {
        val config = target.extensions.create<StickyNoteLoaderExtension>("stickynote", target)

        val createStickyNoteLoader by target.tasks.creating(StickyNoteTask::class) {
            group = "stickynote"
            description = "stickynote configurations and setup task"

            val finalVersion = if (config.loaderVersion.get() == "0.0.0") {
                this::class.java.`package`.implementationVersion
                    ?: throw IllegalStateException("Cannot determine the plugin version")
            } else config.loaderVersion.get()
            config.loaderVersion.set(finalVersion)

            this.outputDir.set(config.outputDirectory)
            this.loaderVersion.set(config.loaderVersion.get())
            this.modules.set(config.modules)
            this.relocation.set(config.relocation)
            this.useKotlin.set(config.useKotlin)
        }

        target.tasks.withType<JavaCompile> {
            dependsOn(createStickyNoteLoader)
        }

        target.plugins.apply("io.github.goooler.shadow")
        target.tasks.withType<ShadowJar> {
            /*val manualRelocations = mapOf(
                "com.github.patheloper.pathetic" to "patheloper",
                "com.github.cryptomorin" to "cryptomorin",
                "com.google.code.gson" to "gson"
            )
            val versionCatalogs = extensions.getByType(VersionCatalogsExtension::class.java)
            val libs = versionCatalogs.named("stickyNoteLibs")

            for (bundleAlias in libs.bundleAliases) {
                val bundle = libs.findBundle(bundleAlias).get().get()
                for (alias in bundle) {
                    if (alias.module.name.contains("stickynote") || alias.module.name == "kotlin-stdlib" || alias.module.name == "kotlin-reflect") {

                    } else {
                        if (manualRelocations.contains(alias.group)) {
                            val relocation = manualRelocations[alias.group]!!
                            relocate(relocation, "${config.relocation.get().second}.libs.${relocation}")
                        } else {
                            relocate(alias.group, "${config.relocation.get().second}.libs.${alias.group!!.split(".").lastOrNull()!!}")
                        }
                    }
                }
            }*/
            relocate(config.relocation.get().first, config.relocation.get().second)
        }

        @Suppress("UNCHECKED_CAST")
        runCatching { Class.forName("org.jetbrains.kotlin.gradle.tasks.KotlinCompile") as Class<Task> }
            .onSuccess { klass ->
                target.tasks.withType(klass) {
                    dependsOn(createStickyNoteLoader)
                }
            }

        target.repositories {
            mavenLocal()
            mavenCentral()

            maven {
                name = "sayandev"
                setUrl("https://repo.sayandev.org/snapshots")
            }
        }

        target.afterEvaluate {
            require(createStickyNoteLoader.loaderVersion.get() != "0.0.0") { "loaderVersion is not provided" }
            val defaultLocation = layout.buildDirectory.dir("stickynote/output").get().asFile

            config.relocation.set(Pair("org.sayandev.stickynote", "${project.group}.${project.name.lowercase()}"))

            if (config.outputDirectory.get().asFile == defaultLocation) {
                extensions.getByType<JavaPluginExtension>().sourceSets["main"].java.srcDir(defaultLocation)
            }

            if (!config.useKotlin.get()) {
                project.dependencies.add("compileOnly", "org.jetbrains.kotlin:kotlin-stdlib:${KotlinVersion.CURRENT}")
            }
            project.dependencies.add("compileOnly", "org.sayandev:stickynote-core-all:${createStickyNoteLoader.loaderVersion.get()}")

            if (config.modules.get().map { it.type }.contains(StickyNoteModules.BUKKIT)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-bukkit-all:${createStickyNoteLoader.loaderVersion.get()}")
            }
            if (config.modules.get().map { it.type }.contains(StickyNoteModules.VELOCITY)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-velocity-all:${createStickyNoteLoader.loaderVersion.get()}")
            }
            if (config.modules.get().map { it.type }.contains(StickyNoteModules.BUNGEECORD)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-bungeecord-all:${createStickyNoteLoader.loaderVersion.get()}")
            }

            createStickyNoteLoader.run()
        }
    }
}