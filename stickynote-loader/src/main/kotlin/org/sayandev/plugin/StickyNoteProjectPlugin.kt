package org.sayandev.plugin

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import kotlin.jvm.optionals.getOrNull

class StickyNoteProjectPlugin : Plugin<Project> {

    private fun VersionCatalog.findBundleCompat(alias: String) =
        sequenceOf(
            alias,
            alias.replace('-', '.'),
            alias.replace('.', '-')
        )
            .mapNotNull { findBundle(it).getOrNull() }
            .firstOrNull()

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
            if (config.modules.get().none { it.moduleId == StickyNoteModuleRegistry.CORE }) {
                config.modules.add(ModuleConfiguration(StickyNoteModuleRegistry.CORE, finalVersion))
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
            if (config.modules.get().none { it.moduleId == StickyNoteModuleRegistry.CORE }) {
                config.modules.add(ModuleConfiguration(StickyNoteModuleRegistry.CORE, createStickyNoteLoader.loaderVersion.get()))
            }

            val configuredModules = config.modules.get()
            val moduleVersionById = configuredModules.associate { it.moduleId to it.version }
            val resolvedModuleDefinitions = StickyNoteModuleRegistry.resolveDefinitions(configuredModules.map { it.moduleId }.toSet())
            val moduleExcludedDependencies = resolvedModuleDefinitions
                .flatMap { it.excludedDependencies }
                .toSet()
            val moduleExcludedFilePatterns = resolvedModuleDefinitions
                .flatMap { it.excludedFilePatterns }
                .toSet()
            val excludedCoordinates = moduleExcludedDependencies
                .mapNotNull { token ->
                    val split = token.split(":")
                    if (split.size == 2 && split[1] != "*") split[0] to split[1] else null
                }
                .toSet()
            val excludedGroups = moduleExcludedDependencies
                .mapNotNull { token ->
                    val split = token.split(":")
                    if (split.size == 2 && split[1] == "*") split[0] else null
                }
                .toSet()
            val excludedArtifacts = moduleExcludedDependencies
                .filter { !it.contains(":") }
                .toSet()

            fun isExcludedDependency(group: String?, name: String): Boolean {
                return excludedCoordinates.any { (excludedGroup, excludedName) ->
                    excludedGroup == group && excludedName == name
                } || excludedGroups.contains(group) || excludedArtifacts.contains(name)
            }

            if (config.packagingMode.get() == StickyNotePackagingMode.FAT) {
                project.configurations.findByName("runtimeClasspath")?.let { runtimeClasspath ->
                    excludedGroups.forEach { group ->
                        runtimeClasspath.exclude(mapOf("group" to group))
                    }
                    excludedCoordinates.forEach { (group, module) ->
                        runtimeClasspath.exclude(mapOf("group" to group, "module" to module))
                    }
                    excludedArtifacts.forEach { module ->
                        runtimeClasspath.exclude(mapOf("module" to module))
                    }
                }

                target.tasks.withType<ShadowJar>().configureEach {
                    moduleExcludedFilePatterns.forEach { excludedPattern ->
                        exclude(excludedPattern)
                    }
                    dependencies {
                        excludedGroups.forEach { group ->
                            exclude(dependency("$group:.*"))
                        }
                        excludedCoordinates.forEach { (group, module) ->
                            exclude(dependency("$group:$module"))
                        }
                    }
                }
            }

            createStickyNoteLoader.modules.set(
                resolvedModuleDefinitions.map { definition ->
                    ModuleConfiguration(
                        definition.id,
                        moduleVersionById[definition.id] ?: createStickyNoteLoader.loaderVersion.get()
                    )
                }
            )

            val artifactVersions = linkedMapOf<String, String>()
            resolvedModuleDefinitions.forEach { definition ->
                val version = moduleVersionById[definition.id] ?: createStickyNoteLoader.loaderVersion.get()
                definition.artifacts.forEach { artifact ->
                    artifactVersions.putIfAbsent(artifact, version)
                }
            }

            for ((artifact, version) in artifactVersions) {
                val notation = "org.sayandev:$artifact:$version"

                when (config.packagingMode.get()) {
                    StickyNotePackagingMode.FAT -> {
                        // FAT mode must include Stickynote transitives in runtime/shadow classpath.
                        project.dependencies.add("compileOnlyApi", notation)
                        val dependency = project.dependencies.add("implementation", notation)
                        if (dependency is ExternalModuleDependency) {
                            excludedGroups.forEach { group ->
                                dependency.exclude(mapOf("group" to group))
                            }
                            excludedCoordinates.forEach { (group, module) ->
                                dependency.exclude(mapOf("group" to group, "module" to module))
                            }
                        }
                    }
                    StickyNotePackagingMode.LOADER_ONLY -> {
                        project.dependencies.add("compileOnlyApi", notation)
                    }
                }

                project.dependencies.add("testImplementation", notation)
            }

            if (config.packagingMode.get() == StickyNotePackagingMode.FAT) {
                resolvedModuleDefinitions
                    .flatMap { it.bundles }
                    .distinct()
                    .forEach { bundleAlias ->
                        libs.findBundleCompat(bundleAlias)?.get()?.forEach { bundleDependency ->
                            if (isExcludedDependency(bundleDependency.module.group, bundleDependency.name)) return@forEach
                            project.dependencies.add("implementation", bundleDependency)
                        }
                    }
            }
//            project.dependencies.add("compileOnlyApi", "org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")
//            project.dependencies.add("testImplementation", "org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}")

            resolvedModuleDefinitions
                .flatMap { it.loaderArtifacts }
                .distinct()
                .forEach { loaderArtifact ->
                    val dependency = project.dependencies.add("implementation", "org.sayandev:$loaderArtifact:${createStickyNoteLoader.loaderVersion.get()}")
                    if (config.packagingMode.get() == StickyNotePackagingMode.FAT && dependency is ExternalModuleDependency) {
                        excludedGroups.forEach { group ->
                            dependency.exclude(mapOf("group" to group))
                        }
                        excludedCoordinates.forEach { (group, module) ->
                            dependency.exclude(mapOf("group" to group, "module" to module))
                        }
                    }
                }

            createStickyNoteLoader.run()
        }
    }
}
