package org.sayandev.plugin

import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.sayandev.plugin.output.ClassGenerator

abstract class StickyNoteTask : DefaultTask() {

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val modules: ListProperty<ModuleConfiguration>

    @get:Input
    abstract val loaderVersion: Property<String>

    @get:Input
    abstract val relocation: Property<Pair<String, String>>

    @get:Input
    abstract val relocate: Property<Boolean>

    @get:Input
    abstract val useKotlin: Property<Boolean>

    @get:Input
    abstract val useSubmodule: Property<Boolean>

    @get:Input
    abstract val stickyLoadDependencies: ListProperty<StickyLoadDependency>

    @get:Input
    abstract val packagingMode: Property<StickyNotePackagingMode>

    @TaskAction
    @KotlinPoetJavaPoetPreview
    fun run() {
        val classGenerator = ClassGenerator(
            project = project,
            outputDir = outputDir.get(),
            modules = modules.get(),
            relocate = relocate.get(),
            relocation = relocation.get(),
            stickyLoadDependencies = stickyLoadDependencies.get(),
            packagingMode = packagingMode.get()
        )
        classGenerator.generateRelocationClass()
        classGenerator.generateDependencyClass()
        classGenerator.generateStickyNotesClass()
    }
}
