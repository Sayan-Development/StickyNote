package org.sayandev

val kotlinPoet = Dependency(
    group = "com.squareup",
    artifact = "kotlinpoet",
    version = "1.17.0",
    relocation = Relocation("com.squareup", "org.sayandev.stickynote.lib.squareup"),
    type = Dependency.Type.API,
    modules = listOf(Module.LOADER)
)

val kotlinPoetJava = Dependency(
    group = "com.squareup",
    artifact = "kotlinpoet-javapoet",
    version = "1.17.0",
    relocation = Relocation("com.squareup", "org.sayandev.stickynote.lib.squareup"),
    type = Dependency.Type.API,
    modules = listOf(Module.LOADER)
)

val configurateYaml = Dependency(
    group = "org.spongepowered",
    artifact = "configurate-yaml",
    version = "4.1.2",
    relocation = Relocation("org.spongepowered", "org.sayandev.stickynote.lib.spongepowered"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val snakeYaml = Dependency(
    group = "org.yaml",
    artifact = "snakeyaml",
    version = "2.2",
    relocation = Relocation("org.yaml", "org.sayandev.stickynote.lib.yaml"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val configurateExtraKotlin = Dependency(
    group = "org.spongepowered",
    artifact = "configurate-extra-kotlin",
    version = "4.1.2",
    relocation = Relocation("org.spongepowered", "org.sayandev.stickynote.lib.spongepowered"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val foliaAPI = Dependency(
    group = "dev.folia",
    artifact = "folia-api",
    version = "1.20.4-R0.1-SNAPSHOT",
    relocation = null,
    type = Dependency.Type.COMPILE_ONLY,
    modules = listOf(Module.BUKKIT, Module.PAPER, Module.LOADER_BUKKIT)
)

val velocityAPI = Dependency(
    group = "com.velocitypowered",
    artifact = "velocity-api",
    version = "3.3.0-SNAPSHOT",
    relocation = null,
    type = Dependency.Type.COMPILE_ONLY_API,
    modules = listOf(Module.VELOCITY, Module.LOADER_VELOCITY)
)

val velocityAPIAnnotation = Dependency(
    group = "com.velocitypowered",
    artifact = "velocity-api",
    version = "3.3.0-SNAPSHOT",
    relocation = null,
    type = Dependency.Type.ANNOTATION_PROCESSOR,
    modules = listOf(Module.VELOCITY, Module.LOADER_VELOCITY)
)

/*val spigotAPI = Dependency(
    group = "org.spigotmc",
    artifact = "spigot-api",
    version = "1.20.5-R0.1-SNAPSHOT",
    relocation = null,
    type = Dependency.Type.COMPILE_ONLY,
    modules = listOf(Module.BUKKIT, Module.PAPER)
)*/

val paperApi = Dependency(
    group = "io.papermc.paper",
    artifact = "paper-api",
    version = "1.20.6-R0.1-SNAPSHOT",
    relocation = null,
    type = Dependency.Type.COMPILE_ONLY,
    modules = listOf(Module.BUKKIT, Module.PAPER, Module.LOADER_BUKKIT)
)

val bungeecordAPI = Dependency(
    group = "net.md-5",
    artifact = "bungeecord-api",
    version = "1.19-R0.1-SNAPSHOT",
    relocation = null,
    type = Dependency.Type.COMPILE_ONLY,
    modules = listOf(Module.BUNGEECORD, Module.LOADER_BUNGEECORD)
)

val cloudCore = Dependency(
    group = "org.incendo",
    artifact = "cloud-core",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val cloudPaper = Dependency(
    group = "org.incendo",
    artifact = "cloud-paper",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUKKIT, Module.PAPER)
)

val cloudMinecraftExtras = Dependency(
    group = "org.incendo",
    artifact = "cloud-minecraft-extras",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUKKIT, Module.PAPER)
)

val cloudKotlinExtension = Dependency(
    group = "org.incendo",
    artifact = "cloud-kotlin-extensions",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUKKIT, Module.PAPER)
)

val cloudAnnotations = Dependency(
    group = "org.incendo",
    artifact = "cloud-annotations",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUKKIT, Module.PAPER)
)

val cloudKotlinCoroutines = Dependency(
    group = "org.incendo",
    artifact = "cloud-kotlin-coroutines",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUKKIT, Module.PAPER)
)

val cloudKotlinCoroutinesAnnotations = Dependency(
    group = "org.incendo",
    artifact = "cloud-kotlin-coroutines-annotations",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUKKIT, Module.PAPER)
)

val inventoryFramework = Dependency(
    group = "com.github.stefvanschie.inventoryframework",
    artifact = "IF",
    version = "0.10.13",
    relocation = Relocation("com.github.stefvanschie.inventoryframework", "org.sayandev.stickynote.lib.inventoryframework"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUKKIT, Module.PAPER)
)

val xSeries = Dependency(
    group = "com.github.cryptomorin",
    artifact = "XSeries",
    version = "11.2.0",
    relocation = Relocation("com.cryptomorin.xseries", "org.sayandev.stickynote.lib.xseries"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUKKIT, Module.PAPER)
)

val adventureAPI = Dependency(
    group = "net.kyori",
    artifact = "adventure-api",
    version = "4.17.0",
    relocation = Relocation("net.kyori", "org.sayandev.stickynote.lib.kyori"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val adventureTextSerializerGson = Dependency(
    group = "net.kyori",
    artifact = "adventure-text-serializer-gson",
    version = "4.17.0",
    relocation = Relocation("net.kyori", "org.sayandev.stickynote.lib.kyori"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val adventureTextMiniMessage = Dependency(
    group = "net.kyori",
    artifact = "adventure-text-minimessage",
    version = "4.17.0",
    relocation = Relocation("net.kyori", "org.sayandev.stickynote.lib.kyori"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val adventurePlatformBukkit = Dependency(
    group = "net.kyori",
    artifact = "adventure-platform-bukkit",
    version = "4.3.3",
    relocation = Relocation("net.kyori", "org.sayandev.stickynote.lib.kyori"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUKKIT)
)

val adventurePlatformBungeecord = Dependency(
    group = "net.kyori",
    artifact = "adventure-platform-bungeecord",
    version = "4.3.3",
    relocation = Relocation("net.kyori", "org.sayandev.stickynote.lib.kyori"),
    type = Dependency.Type.API,
    modules = listOf(Module.BUNGEECORD)
)

val mysqlConnector = Dependency(
    group = "com.mysql",
    artifact = "mysql-connector-j",
    version = "8.4.0",
    relocation = Relocation("com.mysql", "org.sayandev.stickynote.lib.mysql"),
    type = Dependency.Type.API,
    modules = listOf(/*Module.CORE, Module.BUKKIT*/)
)

val jedis = Dependency(
    group = "redis.clients",
    artifact = "jedis",
    version = "5.0.0",
    relocation = Relocation("redis", "org.sayandev.stickynote.lib.jedis"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val reflections = Dependency(
    group = "org.reflections",
    artifact = "reflections",
    version = "0.10.2",
    relocation = Relocation("org.reflections", "org.sayandev.stickynote.lib.reflections"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val hikari = Dependency(
    group = "com.zaxxer",
    artifact = "HikariCP",
    version = "5.1.0",
    relocation = Relocation("com.zaxxer", "org.sayandev.stickynote.lib.zaxxer"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val guava = Dependency(
    group = "com.google.guava",
    artifact = "guava",
    version = "31.1-jre",
    relocation = Relocation("com.google.guava", "org.sayandev.stickynote.lib.guava"),
    type = Dependency.Type.COMPILE_ONLY_API,
    modules = listOf(Module.CORE)
)

val guavaTestImplementation = Dependency(
    group = "com.google.guava",
    artifact = "guava",
    version = "31.1-jre",
    relocation = Relocation("com.google.guava", "org.sayandev.stickynote.lib.guava"),
    type = Dependency.Type.TEST_IMPLEMENTATION,
    modules = listOf(Module.CORE)
)

val gson = Dependency(
    group = "com.google.code.gson",
    artifact = "gson",
    version = "2.10.1",
//    relocation = Relocation("com.google.gson", "org.sayandev.stickynote.lib.gson"),
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val kotlinReflect = Dependency(
    group = "org.jetbrains.kotlin",
    artifact = "kotlin-reflect",
    version = "2.0.0",
    relocation = null,
    type = Dependency.Type.API,
    modules = listOf(Module.CORE)
)

val libbyBukkit = Dependency(
    group = "com.alessiodp.libby",
    artifact = "libby-bukkit",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("com.alessiodp.libby", "org.sayandev.stickynote.lib.libby"),
    type = Dependency.Type.API,
    modules = listOf(Module.LOADER_BUKKIT),
    shadeMethod = Dependency.ShadeMethod.FORCE
)

val libbyVelocity = Dependency(
    group = "com.alessiodp.libby",
    artifact = "libby-velocity",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("com.alessiodp.libby", "org.sayandev.stickynote.lib.libby"),
    type = Dependency.Type.API,
    modules = listOf(Module.LOADER_VELOCITY),
    shadeMethod = Dependency.ShadeMethod.FORCE
)

val libbyBungeecord = Dependency(
    group = "com.alessiodp.libby",
    artifact = "libby-bungee",
    version = "2.0.0-SNAPSHOT",
    relocation = Relocation("com.alessiodp.libby", "org.sayandev.stickynote.lib.libby"),
    type = Dependency.Type.API,
    modules = listOf(Module.LOADER_BUNGEECORD),
    shadeMethod = Dependency.ShadeMethod.FORCE
)

fun repositories() = listOf(
    Repository(
        id = "spongepowered",
        repos = listOf("https://repo.spongepowered.org/maven/"),
        dependencies = listOf(
            configurateYaml,
            configurateExtraKotlin,
        )
    ),
    Repository(
        id = "papermc",
        repos = listOf("https://repo.papermc.io/repository/maven-public/"),
        dependencies = listOf(
            foliaAPI,
            velocityAPI,
            velocityAPIAnnotation,
            paperApi
        )
    ),
    /*Repository(
        id = "spigotmc",
        repos = listOf("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"),
        dependencies = listOf(
            spigotAPI
        )
    ),*/
    Repository(
        id = "sonatype-snapshots",
        repos = listOf("https://oss.sonatype.org/content/repositories/snapshots"),
        dependencies = listOf(
            cloudCore,
            cloudPaper,
            cloudMinecraftExtras,
            cloudKotlinExtension,
            cloudAnnotations,
            cloudKotlinCoroutines,
            cloudKotlinCoroutinesAnnotations,
            bungeecordAPI
        )
    ),
    Repository(
        id = "inventoryframework",
        repos = emptyList(),
        dependencies = listOf(
            inventoryFramework,
        )
    ),
    Repository(
        id = "central",
        repos = emptyList(),
        dependencies = listOf(
            xSeries,
            adventureAPI,
            adventureTextSerializerGson,
            adventureTextMiniMessage,
            adventurePlatformBukkit,
            adventurePlatformBungeecord,
            mysqlConnector,
            jedis,
            reflections,
            hikari,
            guava,
            guavaTestImplementation,
            gson,
            kotlinReflect,
            kotlinPoet,
            kotlinPoetJava,
            snakeYaml
        )
    ),
    Repository(
        id = "sonatype",
        repos = listOf("https://s01.oss.sonatype.org/content/repositories/snapshots/"),
        dependencies = listOf(
            libbyBukkit,
            libbyVelocity,
            libbyBungeecord,
        )
    ),
)