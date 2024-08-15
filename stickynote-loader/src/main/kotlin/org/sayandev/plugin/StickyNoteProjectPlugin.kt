package org.sayandev.plugin

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.*
import kotlin.jvm.internal.Intrinsics.Kotlin

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
            println("Using stickynote version $finalVersion")
            config.loaderVersion.set(finalVersion)

            this.outputDir.set(config.outputDirectory)
            this.loaderVersion.set(config.loaderVersion.get())
            if (!config.modules.get().map { it.type }.contains(StickyNoteModules.CORE)) {
                config.modules.add(ModuleConfiguration(StickyNoteModules.CORE, finalVersion))
            }
            this.modules.set(config.modules)
            this.relocation.set(config.relocation)
            this.useKotlin.set(config.useKotlin)
        }

        target.plugins.apply("io.github.goooler.shadow")

        target.repositories {
            mavenLocal()
            mavenCentral()

            maven {
                name = "sayandev"
                setUrl("https://repo.sayandev.org/snapshots")
            }
            maven {
                name = "extendedclip"
                setUrl("https://repo.extendedclip.com/content/repositories/placeholderapi/")
            }
            maven {
                name = "spongepowered"
                setUrl("https://repo.spongepowered.org/maven/")
            }
            maven {
                name = "papermc"
                setUrl("https://repo.papermc.io/repository/maven-public/")
            }
            maven {
                name = "spigotmc"
                setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
            }
            maven {
                name = "sonatype-snapshots"
                setUrl("https://oss.sonatype.org/content/repositories/snapshots")
            }
            maven {
                name = "alessiodp"
                setUrl("https://repo.alessiodp.com/snapshots")
            }
            maven {
                name = "jitpack"
                setUrl("https://jitpack.io")
            }
            maven {
                name = "codemc"
                setUrl("https://repo.codemc.org/repository/maven-public/")
            }
        }

        target.afterEvaluate {
            val versionCatalogs = target.extensions.getByType(VersionCatalogsExtension::class.java)
            val libs = versionCatalogs.named("stickyNoteLibs")

            target.tasks.withType<ShadowJar> {
                for (bundleAlias in libs.bundleAliases) {
                    val bundle = libs.findBundle(bundleAlias).get().get()
                    for (alias in bundle) {
                        if (alias.module.name.contains("stickynote") || alias.module.name == "kotlin-stdlib" || alias.module.name == "kotlin-reflect") continue
                        relocate(alias.group, "${target.group}.${target.name.lowercase()}.libs.${alias.group.split(".").last()}")
                    }
                }
                relocate("org.sayandev.stickynote", "${target.group}.${target.name.lowercase()}")
                mergeServiceFiles()
            }

            require(createStickyNoteLoader.loaderVersion.get() != "0.0.0") { "loaderVersion is not provided" }
            val defaultLocation = layout.buildDirectory.dir("stickynote/output").get().asFile

            config.relocation.set(Pair("org.sayandev.stickynote", "${project.group}.${project.name.lowercase()}"))

            if (config.outputDirectory.get().asFile == defaultLocation) {
                extensions.getByType<JavaPluginExtension>().sourceSets["main"].java.srcDir(defaultLocation)
            }

            project.dependencies.add("compileOnly", "org.sayandev:stickynote-core-shaded:${createStickyNoteLoader.loaderVersion.get()}")
            project.dependencies.add("compileOnly", "org.jetbrains.kotlin:kotlin-stdlib:${KotlinVersion.CURRENT }")

            if (config.modules.get().map { it.type }.contains(StickyNoteModules.BUKKIT)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-bukkit-shaded:${createStickyNoteLoader.loaderVersion.get()}")
            }
            if (config.modules.get().map { it.type }.contains(StickyNoteModules.VELOCITY)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-velocity-shaded:${createStickyNoteLoader.loaderVersion.get()}")
            }
            if (config.modules.get().map { it.type }.contains(StickyNoteModules.BUNGEECORD)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-bungeecord-shaded:${createStickyNoteLoader.loaderVersion.get()}")
            }

            createStickyNoteLoader.run()
        }
    }
}