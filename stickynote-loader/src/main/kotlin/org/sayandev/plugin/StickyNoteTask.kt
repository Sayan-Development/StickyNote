package org.sayandev.plugin

import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
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
    abstract val stickyLoadDependencies: ListProperty<StickyLoadDependency>

    @TaskAction
    @KotlinPoetJavaPoetPreview
    fun run() {
        val lateDependencies = listOf(
            "sayan"
        )
        val lateBundles = mutableListOf<MinimalExternalModuleDependency>()
        val versionCatalogs = project.extensions.getByType(VersionCatalogsExtension::class.java)
        val libs = versionCatalogs.named("stickyNoteLibs")
        for (bundleAlias in libs.bundleAliases) {
            for (library in libs.findBundle(bundleAlias).get().get()) {
                if (library.name.contains("libby")) continue
                if (library.group.contains("alessiodp")) continue
                if (project.configurations.getByName("implementation").dependencies.any { it.name == library.name }) continue
                if (lateDependencies.any { library.name.contains(it) }) {
                    lateBundles.add(library)
                    continue
                }
                project.dependencies.add("compileOnly", "${library.group}:${library.name}:${library.version}")
            }
        }

        for (library in lateBundles) {
            project.dependencies.add("compileOnly", "${library.group}:${library.name}:${library.version}")
        }

        for (libraryAlias in versionCatalogs.named("stickyNoteLibs").libraryAliases) {
            val library = libs.findLibrary(libraryAlias).get().get()
            project.dependencies.add("testImplementation", "${library.group}:${library.name}:${library.version}")
        }


        for (module in modules.get()) {
            project.dependencies.add("compileOnly", "org.sayandev:${module.type.artifact}:${module.version}")
            project.dependencies.add("testImplementation", "org.sayandev:${module.type.artifact}:${module.version}")
        }

        val classGenerator = ClassGenerator(project, outputDir.get(), modules.get(), relocate.get(), relocation.get(), stickyLoadDependencies.get())
        classGenerator.generateRelocationClass()
        classGenerator.generateDependencyClass()
        classGenerator.generateStickyNotesClass()
    }
}