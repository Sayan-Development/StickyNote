package org.sayandev.plugin

@DslMarker
annotation class StickyNoteDsl

class StickyNoteModuleSelection internal constructor(
    internal val moduleIds: Set<String>
)

sealed interface StickyNoteModuleReference {
    val moduleId: String

    fun asSelection(): StickyNoteModuleSelection = StickyNoteModuleSelection(setOf(moduleId))
}

class SimpleStickyNoteModuleReference(
    override val moduleId: String
) : StickyNoteModuleReference

class CoreStickyNoteModuleReference : StickyNoteModuleReference {
    override val moduleId: String = StickyNoteModuleRegistry.CORE

    operator fun invoke(block: CoreModuleScope.() -> Unit): StickyNoteModuleSelection {
        val scope = CoreModuleScope().apply(block)
        return StickyNoteModuleSelection(setOf(moduleId) + scope.selectedModuleIds())
    }
}

@StickyNoteDsl
class CoreModuleScope internal constructor() {
    private val moduleIds = linkedSetOf<String>()

    val database: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.CORE_DATABASE
        }

    val configuration: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.CORE_CONFIGURATION
        }

    val configurationKotlinx: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.CORE_CONFIGURATION_KOTLINX
        }

    val configurationConfigurate: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.CORE_CONFIGURATION_CONFIGURATE
        }

    fun configuration(block: CoreConfigurationModuleScope.() -> Unit) {
        val scope = CoreConfigurationModuleScope().apply(block)
        moduleIds += scope.selectedModuleIds()
    }

    val messaging: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.CORE_MESSAGING
        }

    fun messaging(block: CoreMessagingModuleScope.() -> Unit) {
        val scope = CoreMessagingModuleScope().apply(block)
        moduleIds += StickyNoteModuleRegistry.CORE_MESSAGING
        moduleIds += scope.selectedModuleIds()
    }

    internal fun selectedModuleIds(): Set<String> = moduleIds
}

@StickyNoteDsl
class CoreConfigurationModuleScope internal constructor() {
    private val moduleIds = linkedSetOf<String>()

    val kotlinx: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.CORE_CONFIGURATION_KOTLINX
        }

    val configurate: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.CORE_CONFIGURATION_CONFIGURATE
        }

    internal fun selectedModuleIds(): Set<String> = moduleIds
}

@StickyNoteDsl
class CoreMessagingModuleScope internal constructor() {
    private val moduleIds = linkedSetOf<String>()

    val redis: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.CORE_MESSAGING_REDIS
        }

    val websocket: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.CORE_MESSAGING_WEBSOCKET
        }

    internal fun selectedModuleIds(): Set<String> = moduleIds
}

class PaperStickyNoteModuleReference : StickyNoteModuleReference {
    override val moduleId: String = StickyNoteModuleRegistry.PAPER

    operator fun invoke(block: PaperModuleScope.() -> Unit): StickyNoteModuleSelection {
        val scope = PaperModuleScope().apply(block)
        return StickyNoteModuleSelection(setOf(moduleId) + scope.selectedModuleIds())
    }
}

@StickyNoteDsl
class PaperModuleScope internal constructor() {
    private val moduleIds = linkedSetOf<String>()

    val command: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.PAPER_COMMAND
        }

    val commands: Unit
        get() = command

    val gui: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.PAPER_GUI
        }

    val nms: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.PAPER_NMS
        }

    val cloud: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.PAPER_CLOUD
        }

    internal fun selectedModuleIds(): Set<String> = moduleIds
}

class ProxyStickyNoteModuleReference : StickyNoteModuleReference {
    override val moduleId: String = StickyNoteModuleRegistry.PROXY

    operator fun invoke(block: ProxyModuleScope.() -> Unit): StickyNoteModuleSelection {
        val scope = ProxyModuleScope().apply(block)
        return StickyNoteModuleSelection(setOf(moduleId) + scope.selectedModuleIds())
    }
}

@StickyNoteDsl
class ProxyModuleScope internal constructor() {
    private val moduleIds = linkedSetOf<String>()

    val velocity: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.PROXY_VELOCITY
        }

    fun velocity(block: ProxyVelocityModuleScope.() -> Unit) {
        val scope = ProxyVelocityModuleScope().apply(block)
        moduleIds += StickyNoteModuleRegistry.PROXY_VELOCITY
        moduleIds += scope.selectedModuleIds()
    }

    val bungeecord: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.PROXY_BUNGEECORD
        }

    internal fun selectedModuleIds(): Set<String> = moduleIds
}

@StickyNoteDsl
class ProxyVelocityModuleScope internal constructor() {
    private val moduleIds = linkedSetOf<String>()

    val command: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.PROXY_VELOCITY_COMMAND
        }

    val commands: Unit
        get() = command

    val cloud: Unit
        get() {
            moduleIds += StickyNoteModuleRegistry.PROXY_VELOCITY_CLOUD
        }

    internal fun selectedModuleIds(): Set<String> = moduleIds
}
