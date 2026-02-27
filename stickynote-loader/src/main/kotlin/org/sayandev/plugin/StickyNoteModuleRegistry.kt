package org.sayandev.plugin

data class StickyNoteModuleDefinition(
    val id: String,
    val artifacts: Set<String> = emptySet(),
    val bundles: Set<String> = emptySet(),
    val implies: Set<String> = emptySet(),
    val loaderArtifacts: Set<String> = emptySet(),
    val excludedDependencies: Set<String> = emptySet(),
    val excludedFilePatterns: Set<String> = emptySet()
)

object StickyNoteModuleRegistry {
    const val CORE = "core"
    const val CORE_DATABASE = "core.database"
    const val CORE_CONFIGURATION = "core.configuration"
    const val CORE_CONFIGURATION_KOTLINX = "core.configuration.kotlinx"
    const val CORE_CONFIGURATION_CONFIGURATE = "core.configuration.configurate"
    const val CORE_MESSAGING = "core.messaging"
    const val CORE_MESSAGING_REDIS = "core.messaging.redis"
    const val CORE_MESSAGING_WEBSOCKET = "core.messaging.websocket"

    const val PAPER = "paper"
    const val PAPER_GUI = "paper.gui"
    const val PAPER_NMS = "paper.nms"
    const val PAPER_COMMAND = "paper.command"
    const val PAPER_CLOUD = "paper.cloud"

    const val PROXY = "proxy"
    const val PROXY_VELOCITY = "proxy.velocity"
    const val PROXY_VELOCITY_COMMAND = "proxy.velocity.command"
    const val PROXY_VELOCITY_CLOUD = "proxy.velocity.cloud"
    const val PROXY_BUNGEECORD = "proxy.bungeecord"

    const val CLOUD = "cloud"
    const val COMMAND = "command"

    private val moduleDefinitions = listOf(
        StickyNoteModuleDefinition(
            id = CORE,
            artifacts = setOf("stickynote-core"),
            bundles = setOf("implementation-core-base")
        ),
        StickyNoteModuleDefinition(
            id = CORE_DATABASE,
            artifacts = setOf("stickynote-database"),
            bundles = setOf("implementation-core-database"),
            implies = setOf(CORE)
        ),
        StickyNoteModuleDefinition(
            id = CORE_CONFIGURATION,
            artifacts = setOf(
                "stickynote-configuration-kotlinx",
                "stickynote-configuration-configurate",
            ),
            bundles = setOf(
                "implementation-core-configuration-kotlinx",
                "implementation-core-configuration-configurate",
            ),
            implies = setOf(CORE)
        ),
        StickyNoteModuleDefinition(
            id = CORE_CONFIGURATION_KOTLINX,
            artifacts = setOf("stickynote-configuration-kotlinx"),
            bundles = setOf("implementation-core-configuration-kotlinx"),
            implies = setOf(CORE)
        ),
        StickyNoteModuleDefinition(
            id = CORE_CONFIGURATION_CONFIGURATE,
            artifacts = setOf("stickynote-configuration-configurate"),
            bundles = setOf("implementation-core-configuration-configurate"),
            implies = setOf(CORE)
        ),
        StickyNoteModuleDefinition(
            id = CORE_MESSAGING,
            implies = setOf(CORE)
        ),
        StickyNoteModuleDefinition(
            id = CORE_MESSAGING_REDIS,
            artifacts = setOf("stickynote-messaging-redis"),
            bundles = setOf("implementation-core-messaging-redis"),
            implies = setOf(CORE_MESSAGING)
        ),
        StickyNoteModuleDefinition(
            id = CORE_MESSAGING_WEBSOCKET,
            artifacts = setOf("stickynote-messaging-websocket"),
            bundles = setOf("implementation-core-messaging-websocket"),
            implies = setOf(CORE_MESSAGING)
        ),
        StickyNoteModuleDefinition(
            id = COMMAND,
            artifacts = setOf("stickynote-command"),
            implies = setOf(CORE)
        ),
        StickyNoteModuleDefinition(
            id = CLOUD,
            artifacts = setOf("stickynote-cloud"),
            bundles = setOf("implementation-cloud"),
            implies = setOf(CORE)
        ),
        StickyNoteModuleDefinition(
            id = PAPER,
            artifacts = setOf("stickynote-paper"),
            bundles = setOf("implementation-paper-base"),
            implies = setOf(CORE),
            loaderArtifacts = setOf("stickynote-loader-paper"),
            excludedDependencies = setOf(
                "org.spongepowered:configurate-yaml",
                "org.spongepowered:configurate-extra-kotlin",
                "org.yaml:snakeyaml",
                "com.google.code.gson:gson",
                "com.mysql:mysql-connector-j",
                "org.xerial:sqlite-jdbc",
                "com.h2database:h2",
                "com.alessiodp.libby:libby-core",
                "com.alessiodp.libby:libby-bukkit",
                "com.alessiodp.libby:libby-paper",
            )
        ),
        StickyNoteModuleDefinition(
            id = PAPER_GUI,
            bundles = setOf("implementation-paper-gui"),
            implies = setOf(PAPER)
        ),
        StickyNoteModuleDefinition(
            id = PAPER_NMS,
            artifacts = setOf("stickynote-paper-nms"),
            bundles = setOf("implementation-paper-nms"),
            implies = setOf(PAPER)
        ),
        StickyNoteModuleDefinition(
            id = PAPER_COMMAND,
            artifacts = setOf("stickynote-command", "stickynote-command-paper"),
            bundles = setOf("implementation-command-paper"),
            implies = setOf(PAPER)
        ),
        StickyNoteModuleDefinition(
            id = PAPER_CLOUD,
            artifacts = setOf("stickynote-cloud", "stickynote-cloud-paper"),
            bundles = setOf("implementation-cloud", "implementation-cloud-paper"),
            implies = setOf(PAPER)
        ),
        StickyNoteModuleDefinition(
            id = PROXY,
            artifacts = setOf("stickynote-proxy"),
            bundles = setOf("implementation-proxy"),
            implies = setOf(CORE)
        ),
        StickyNoteModuleDefinition(
            id = PROXY_VELOCITY,
            artifacts = setOf("stickynote-proxy-velocity"),
            bundles = setOf("implementation-proxy-velocity-base"),
            implies = setOf(PROXY),
            loaderArtifacts = setOf("stickynote-loader-velocity"),
            excludedDependencies = setOf(
                "org.spongepowered:configurate-yaml",
                "org.spongepowered:configurate-extra-kotlin",
                "org.yaml:snakeyaml",
                "com.google.code.gson:gson",
                "com.google.guava:guava",
                "com.github.ben-manes.caffeine:caffeine",
                "org.checkerframework:checker-qual",
                "com.google.errorprone:error_prone_annotations",
                "com.google.code.findbugs:jsr305",
                "org.slf4j:slf4j-api",
                "net.kyori:*",
                "com.alessiodp.libby:libby-core",
                "com.alessiodp.libby:libby-velocity",
            ),
            excludedFilePatterns = setOf(
                "net/kyori/**",
                "com/google/gson/**",
                "com/google/common/**",
                "com/github/benmanes/**",
                "org/checkerframework/**",
                "com/google/errorprone/**",
                "javax/annotation/**",
                "org/slf4j/**",
                "org/yaml/snakeyaml/**",
                "META-INF/maven/net.kyori/**",
                "META-INF/maven/com.google.code.gson/**",
                "META-INF/maven/com.google.guava/**",
                "META-INF/maven/com.github.ben-manes.caffeine/**",
                "META-INF/maven/org.checkerframework/**",
                "META-INF/maven/com.google.errorprone/**",
                "META-INF/maven/com.google.code.findbugs/**",
                "META-INF/maven/org.slf4j/**",
                "META-INF/maven/org.yaml/**",
            )
        ),
        StickyNoteModuleDefinition(
            id = PROXY_VELOCITY_COMMAND,
            artifacts = setOf("stickynote-command", "stickynote-command-velocity"),
            bundles = setOf("implementation-command-velocity"),
            implies = setOf(PROXY_VELOCITY)
        ),
        StickyNoteModuleDefinition(
            id = PROXY_VELOCITY_CLOUD,
            artifacts = setOf("stickynote-cloud", "stickynote-cloud-velocity"),
            bundles = setOf("implementation-cloud", "implementation-cloud-velocity"),
            implies = setOf(PROXY_VELOCITY)
        ),
        StickyNoteModuleDefinition(
            id = PROXY_BUNGEECORD,
            artifacts = setOf("stickynote-proxy-bungeecord"),
            bundles = setOf("implementation-proxy-bungeecord-base"),
            implies = setOf(PROXY),
            loaderArtifacts = setOf("stickynote-loader-bungeecord")
        )
    )

    private val definitionsById = moduleDefinitions.associateBy { it.id }

    fun definition(id: String): StickyNoteModuleDefinition {
        return definitionsById[id]
            ?: error("Unknown StickyNote module '$id'.")
    }

    fun isKnown(id: String): Boolean = definitionsById.containsKey(id)

    fun resolveModuleIds(selectedModuleIds: Set<String>): Set<String> {
        val resolved = linkedSetOf<String>()

        fun visit(moduleId: String) {
            if (!resolved.add(moduleId)) return
            val definition = definition(moduleId)
            definition.implies.forEach(::visit)
        }

        selectedModuleIds.forEach(::visit)
        return resolved
    }

    fun resolveDefinitions(selectedModuleIds: Set<String>): List<StickyNoteModuleDefinition> {
        val resolvedIds = resolveModuleIds(selectedModuleIds)
        return resolvedIds.map(::definition)
    }
}
