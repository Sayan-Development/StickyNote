package org.sayandev.plugin

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import kotlin.jvm.optionals.getOrNull

class StickyNoteProjectPlugin : Plugin<Project> {

    /**
     * Exclude dependency from relocations. should be the same in StickyNoteLoader
     * @see org.sayandev.loader.common.StickyNoteLoader
     * */
    val relocateExclusion = setOf("kotlin-stdlib", "kotlin-reflect", "kotlin", "kotlin-stdlib-jdk8", "kotlin-stdlib-jdk7", "kotlinx", "kotlinx-coroutines", "kotlinx-coroutines-core-jvm", "takenaka", "mappings", "gson", "adventure")

    @KotlinPoetJavaPoetPreview
    override fun apply(target: Project) {
        val config = target.extensions.create<StickyNoteLoaderExtension>("stickynote", target)

        target.configurations {
            create("stickyload")
        }

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
            if (!config.modules.get().map { it.type }.contains(StickyNoteModules.CORE)) {
                config.modules.add(ModuleConfiguration(StickyNoteModules.CORE, finalVersion))
            }
            this.modules.set(config.modules)
            this.relocate.set(config.relocate)
            this.relocation.set(config.relocation)
            this.useKotlin.set(config.useKotlin)
            this.useSubmodule.set(config.useSubmodule)
            this.packagingMode.set(config.packagingMode)
        }

        target.dependencies.extensions.create("stickynote", StickyLoadDependencyExtension::class.java, target)

        /*target.buildscript {
            repositories { mavenCentral() }

            dependencies {
                val kotlinVersion = "2.2.0"
                classpath(kotlin("gradle-plugin", version = kotlinVersion))
                classpath(kotlin("serialization", version = kotlinVersion))
            }
        }*/

        target.plugins.apply("com.gradleup.shadow")
//        target.plugins.apply("org.jetbrains.kotlin.jvm")
//        target.plugins.apply("org.jetbrains.kotlin.plugin.serialization")
//        target.plugins.apply("java-library")

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
                name = "alessiodp-snapshots"
                setUrl("https://repo.alessiodp.com/snapshots")
            }
            maven {
                name = "alessiodp-releases"
                setUrl("https://repo.alessiodp.com/releases/")
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
            if (config.useSubmodule.get()) {
                val configuredSubmoduleDir = project.rootProject.file(config.submodulePath.get())
                val hasIncludedBuild = project.gradle.includedBuilds.any {
                    it.projectDir.canonicalFile == configuredSubmoduleDir.canonicalFile
                }
                require(hasIncludedBuild) {
                    "StickyNote submodule mode is enabled, but '${configuredSubmoduleDir.path}' is not included as a composite build. " +
                            "Enable it in settings using stickynote.useSubmodule=true and stickynote.submodulePath=${config.submodulePath.get()}."
                }
            }

            val stickyLoadDependencies = mutableListOf<StickyLoadDependency>()
            project.configurations.getByName("stickyload").dependencies.forEach { stickyLoadDependency ->
                var relocation: String? = null
                var rawVersion = stickyLoadDependency.version!!
                if (stickyLoadDependency.version!!.contains("+relocation-")) {
                    stickyLoadDependency.version!!.split("+")[1].removePrefix("relocation-").let { relocation = it }
                    rawVersion = stickyLoadDependency.version!!.split("+")[0]
                }
                stickyLoadDependencies.add(StickyLoadDependency(stickyLoadDependency.group!!, stickyLoadDependency.name, rawVersion, relocation))
                project.dependencies.add("compileOnlyApi", "${stickyLoadDependency.group}:${stickyLoadDependency.name}:${rawVersion}")
                project.dependencies.add("testImplementation", "${stickyLoadDependency.group}:${stickyLoadDependency.name}:${rawVersion}")
            }
//            project.dependencies.add("testImplementation", "org.jetbrains.kotlin:kotlin-test")
            project.dependencies.add("testImplementation", project.dependencies.platform("org.junit:junit-bom:5.12.2"))
            project.dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter")
            project.dependencies.add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
            project.dependencies.add("testImplementation", "ch.qos.logback:logback-classic:1.5.18")

            project.tasks.withType<Test> {
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                }
            }

            createStickyNoteLoader.stickyLoadDependencies.set(stickyLoadDependencies)

            target.tasks.withType<ShadowJar> {
                if (config.relocate.get()) {
                    relocate("org.sayandev.loader", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.loader")
                    relocate("org.sayandev.stickynote", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.stickynote")
                    /*relocate("com.mysql", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.mysql")
//                    relocate("kotlin", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.kotlin")
//                    relocate("org.sqlite", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.sqlite")
                    relocate("kotlinx.coroutines", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.kotlinx.coroutines")
                    relocate("org.jetbrains.exposed", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.exposed")
//                    relocate("com.github.benmanes.caffeine", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.caffeine")
                    for (bundleAlias in libs.bundleAliases.filter { config.modules.get().map { "implementation.".plus(it.type.artifact.removePrefix("stickynote-").replace("-", ".")) }.contains(it) }) {
                        val bundle = libs.findBundle(bundleAlias).get().get()
                        for (alias in bundle) {
                            if (alias.module.group == "org.sayandev") continue
                            if (relocateExclusion.any { alias.module.name == it }) continue
                            // We DON'T relocate adventure to keep compatibility with local paper/velocity adventure api calls
                            if (alias.module.name.contains("adventure")) {
//                            relocate("net.kyori.adventure.text.serializer", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.adventure.text.serializer")
//                            relocate("net.kyori.option", "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.adventure.option")
                                continue
                            }
                            if (alias.module.name == "sqlite-jdbc") continue
                            relocate(alias.group, "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.${alias.group?.split(".")?.last()}")
                        }
                    }
                    for (stickyLoadDependency in stickyLoadDependencies) {
                        if (stickyLoadDependency.relocation != null) {
                            val splitted = stickyLoadDependency.relocation.split(".")
                            relocate(stickyLoadDependency.group, "${target.rootProject.group}.${target.rootProject.name.lowercase()}.lib.${splitted[splitted.size - 1]}")
                        }
                    }*/
                }
                mergeServiceFiles()
            }

            require(createStickyNoteLoader.loaderVersion.get() != "0.0.0") { "loaderVersion is not provided" }
            val defaultLocation = layout.buildDirectory.dir("stickynote/output").get().asFile

            config.relocation.set(Pair("org.sayandev.stickynote", "${project.rootProject.group}.${project.rootProject.name.lowercase()}"))

            if (config.outputDirectory.get().asFile == defaultLocation) {
                extensions.getByType<JavaPluginExtension>().sourceSets["main"].java.srcDir(defaultLocation)
            }

            val libs = target.extensions.getByType(VersionCatalogsExtension::class.java).named("stickyNoteLibs")

            for (module in config.modules.get()) {
                val notation = "org.sayandev:${module.type.artifact}:${module.version}"

                when (config.packagingMode.get()) {
                    StickyNotePackagingMode.FAT -> {
                        // Keep full compile classpath while preventing Stickynote transitives from being shaded.
                        project.dependencies.add("compileOnlyApi", notation)
                        val fatJarDependency = project.dependencies.create(notation).also { dependency ->
                            if (dependency is ModuleDependency) {
                                dependency.isTransitive = false
                            }
                        }
                        project.dependencies.add("implementation", fatJarDependency)

                        val bundleName = module.type.artifact.removePrefix("stickynote-")
                        libs.findBundle("implementation-$bundleName").getOrNull()?.get()?.forEach { bundleDependency ->
                            project.dependencies.add("implementation", bundleDependency)
                        }
                    }
                    StickyNotePackagingMode.LOADER_ONLY -> {
                        project.dependencies.add("compileOnlyApi", notation)
                    }
                }

                project.dependencies.add("testImplementation", notation)
            }
//            project.dependencies.add("compileOnlyApi", "org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
//            project.dependencies.add("testImplementation", "org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")

            if (config.modules.get().map { it.type }.contains(StickyNoteModules.BUKKIT)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-bukkit:${createStickyNoteLoader.loaderVersion.get()}")
            }
            if (config.modules.get().map { it.type }.contains(StickyNoteModules.PAPER)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-paper:${createStickyNoteLoader.loaderVersion.get()}")
            }
            if (config.modules.get().map { it.type }.contains(StickyNoteModules.VELOCITY)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-velocity:${createStickyNoteLoader.loaderVersion.get()}")
            }
            if (config.modules.get().map { it.type }.contains(StickyNoteModules.BUNGEECORD)) {
                project.dependencies.add("implementation", "org.sayandev:stickynote-loader-bungeecord:${createStickyNoteLoader.loaderVersion.get()}")
            }

            createStickyNoteLoader.run()
        }
    }
}
