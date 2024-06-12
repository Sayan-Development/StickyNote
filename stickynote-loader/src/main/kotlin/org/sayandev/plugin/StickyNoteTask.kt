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
    abstract val useLoader: Property<Boolean>

    @get:Input
    abstract val useKotlin: Property<Boolean>

    @get:Input
    abstract val relocate: Property<Boolean>


    @TaskAction
    @KotlinPoetJavaPoetPreview
    fun run() {
        for (module in modules.get()) {
            project.dependencies.add(if (useLoader.get()) "compileOnly" else "implementation", "org.sayandev:${module.type.artifact}:${module.version}")
        }

        val classGenerator = ClassGenerator(project, outputDir.get(), modules.get(), useLoader.get(), relocate.get(), relocation.get())
        classGenerator.generateRelocationClass()
        classGenerator.generateDependencyClass()
        classGenerator.generateStickyNotesClass()
    }
}