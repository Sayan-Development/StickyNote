package org.sayandev.plugin

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class StickyNoteLoaderExtension(protected val project: Project) {

    abstract val modules: ListProperty<ModuleConfiguration>

    abstract val outputDirectory: DirectoryProperty

    abstract val useLoader: Property<Boolean>

    abstract val basePackage: Property<String>

    abstract val loaderVersion: Property<String>

    abstract val relocation: Property<Pair<String, String>>

    abstract val useKotlin: Property<Boolean>

    abstract val relocate: Property<Boolean>

    init {
        outputDirectory.convention(project.layout.buildDirectory.dir("stickynote/output"))
        modules.convention(listOf())
        useLoader.convention(false)
        loaderVersion.convention("0.0.0")
        basePackage.convention(project.group.toString())
        relocate.convention(false)
        relocation.convention("org.sayandev.stickynote" to "${project.group}.libs.stickynote")
        useKotlin.convention(false)
    }

    fun outputDirectory(outputDirectory: Any) {
        this.outputDirectory.set(project.file(outputDirectory))
    }

    fun useLoader(useLoader: Boolean) {
        this.useLoader.set(useLoader)
    }

    fun useKotlin(useKotlin: Boolean) {
        this.useKotlin.set(useKotlin)
    }

    fun relocate(relocate: Boolean) {
        this.relocate.set(relocate)
    }

    fun basePackage(basePackage: String) {
        this.basePackage.set(basePackage)
    }

    fun loaderVersion(loaderVersion: String) {
        this.loaderVersion.set(loaderVersion)
    }

    fun relocation(from: String, to: String) {
        this.relocation.set(from to to)
    }

    fun modules(module: ModulesBuilder.() -> Unit) {
        module(ModulesBuilder(this.modules))
    }

    fun modules(vararg type: StickyNoteModule) {
        type.forEach {
            modules.add(ModuleConfiguration(it, loaderVersion.get()))
        }
    }

    inner class ModulesBuilder(val modules: ListProperty<ModuleConfiguration>) {
        fun module(init: ModuleConfiguration.() -> Unit) {
            val configuration = ModuleConfiguration(StickyNoteModules.NONE, loaderVersion.get())
            configuration.init()
            require(configuration.type != StickyNoteModules.NONE) { "module type cannot be null/none" }
            modules.add(configuration)
        }

        fun module(type: StickyNoteModule) {
            require(type != StickyNoteModules.NONE) { "module type cannot be null/none" }
            modules.add(ModuleConfiguration(type, loaderVersion.get()))
        }
    }

}