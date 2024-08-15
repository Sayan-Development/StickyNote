package org.sayandev.plugin

import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.sayandev.plugin.output.ClassGenerator
import kotlin.jvm.optionals.getOrNull

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
    abstract val useKotlin: Property<Boolean>

    @TaskAction
    @KotlinPoetJavaPoetPreview
    fun run() {
        for (module in modules.get()) {
            println("adding module ${module.type.artifact} from stickynote")
            project.dependencies.add("compileOnly", "org.sayandev:${module.type.artifact}-shaded:${module.version}")
        }

        val versionCatalogs = project.extensions.getByType(VersionCatalogsExtension::class.java)
        val libs = versionCatalogs.named("stickyNoteLibs")

        println("- stickynote-core:")
        for (library in libs.findBundle("implementation-core").get().get()) {
            println("  + ${library.module}")
        }

        for (module in modules.get()) {
            println("- ${module.type.artifact}:")
            val bundleName = module.type.artifact.removePrefix("stickynote-")
            val bundleProvider = libs.findBundle("implementation-${bundleName}").getOrNull()
            if (bundleProvider == null) {
                println("  * Couldn't find bundle for module ${module.type.project} with bundle ${bundleName}")
                continue
            }
            for (library in bundleProvider.get()) {
                println("  + ${library.module}")
            }
        }

        println("relocation: ${relocation.get()}")
        val classGenerator = ClassGenerator(project, outputDir.get(), modules.get(), relocation.get())
        classGenerator.generateRelocationClass()
        classGenerator.generateDependencyClass()
        classGenerator.generateStickyNotesClass()
    }
}