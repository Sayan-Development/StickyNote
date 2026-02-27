package org.sayandev.plugin

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class StickyNoteLoaderExtension(protected val project: Project) {

    abstract val modules: ListProperty<ModuleConfiguration>

    abstract val outputDirectory: DirectoryProperty

    abstract val basePackage: Property<String>

    abstract val loaderVersion: Property<String>

    abstract val relocate: Property<Boolean>

    abstract val relocation: Property<Pair<String, String>>

    abstract val useKotlin: Property<Boolean>

    abstract val useSubmodule: Property<Boolean>

    abstract val submodulePath: Property<String>

    abstract val packagingMode: Property<StickyNotePackagingMode>

    init {
        val configuredPackagingMode = (project.findProperty("stickynote.packagingMode") as? String)
            ?.uppercase()
            ?.let { runCatching { StickyNotePackagingMode.valueOf(it) }.getOrNull() }

        outputDirectory.convention(project.layout.buildDirectory.dir("stickynote/output"))
        loaderVersion.convention("0.0.0")
        modules.convention(listOf())
        basePackage.convention(project.group.toString())
        relocate.convention(true)
        relocation.convention("org.sayandev.stickynote" to "${project.rootProject.group}.${project.rootProject.name.lowercase()}")
        useKotlin.convention(false)
        useSubmodule.convention(project.providers.gradleProperty("stickynote.useSubmodule").map(String::toBoolean).orElse(false))
        submodulePath.convention(project.providers.gradleProperty("stickynote.submodulePath").orElse("stickynote"))
        packagingMode.convention(configuredPackagingMode ?: StickyNotePackagingMode.FAT)
    }

    fun outputDirectory(outputDirectory: Any) {
        this.outputDirectory.set(project.file(outputDirectory))
    }

    fun relocate(relocate: Boolean) {
        this.relocate.set(relocate)
    }

    fun useKotlin(useKotlin: Boolean) {
        this.useKotlin.set(useKotlin)
    }

    fun useSubmodule(useSubmodule: Boolean) {
        this.useSubmodule.set(useSubmodule)
    }

    fun submodulePath(submodulePath: String) {
        this.submodulePath.set(submodulePath)
    }

    fun packagingMode(packagingMode: StickyNotePackagingMode) {
        this.packagingMode.set(packagingMode)
    }

    fun fatJar() {
        packagingMode(StickyNotePackagingMode.FAT)
    }

    fun loaderOnlyJar() {
        packagingMode(StickyNotePackagingMode.LOADER_ONLY)
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

    val core: CoreStickyNoteModuleReference = CoreStickyNoteModuleReference()
    val paper: PaperStickyNoteModuleReference = PaperStickyNoteModuleReference()
    val proxy: ProxyStickyNoteModuleReference = ProxyStickyNoteModuleReference()
    val velocity: SimpleStickyNoteModuleReference = SimpleStickyNoteModuleReference(StickyNoteModuleRegistry.PROXY_VELOCITY)
    val bungeecord: SimpleStickyNoteModuleReference = SimpleStickyNoteModuleReference(StickyNoteModuleRegistry.PROXY_BUNGEECORD)
    val cloud: SimpleStickyNoteModuleReference = SimpleStickyNoteModuleReference(StickyNoteModuleRegistry.CLOUD)
    val command: SimpleStickyNoteModuleReference = SimpleStickyNoteModuleReference(StickyNoteModuleRegistry.COMMAND)
    val configurationKotlinx: SimpleStickyNoteModuleReference = SimpleStickyNoteModuleReference(StickyNoteModuleRegistry.CORE_CONFIGURATION_KOTLINX)
    val configurationConfigurate: SimpleStickyNoteModuleReference = SimpleStickyNoteModuleReference(StickyNoteModuleRegistry.CORE_CONFIGURATION_CONFIGURATE)

    fun register(module: StickyNoteModuleReference) {
        register(module.asSelection())
    }

    fun registerModule(module: StickyNoteModuleReference) {
        register(module)
    }

    fun register(vararg modules: StickyNoteModuleReference) {
        modules.forEach(::register)
    }

    fun registerModule(vararg modules: StickyNoteModuleReference) {
        register(*modules)
    }

    fun register(module: StickyNoteModuleSelection) {
        module.moduleIds.forEach(::registerModuleId)
    }

    fun registerModule(module: StickyNoteModuleSelection) {
        register(module)
    }

    fun register(vararg modules: StickyNoteModuleSelection) {
        modules.forEach(::register)
    }

    fun registerModule(vararg modules: StickyNoteModuleSelection) {
        register(*modules)
    }

    fun modules(vararg modules: StickyNoteModuleReference) {
        register(*modules)
    }

    fun modules(vararg modules: StickyNoteModuleSelection) {
        register(*modules)
    }

    private fun registerModuleId(moduleId: String) {
        require(StickyNoteModuleRegistry.isKnown(moduleId)) { "Unknown StickyNote module '$moduleId'" }
        if (modules.get().any { it.moduleId == moduleId }) return
        modules.add(ModuleConfiguration(moduleId, loaderVersion.get()))
    }

}
