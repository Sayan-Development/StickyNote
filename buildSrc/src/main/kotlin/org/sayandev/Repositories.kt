package org.sayandev

val repositories = listOf(
    Repository(
        id = "spongepowered",
        repos = listOf("https://repo.spongepowered.org/maven/"),
        dependencies = listOf(
            Dependency(
                group = "org.spongepowered",
                artifact = "configurate-yaml",
                version = "4.1.2",
                relocation = Relocation("org.spongepowered", "org.sayandev.stickynote.lib.spongepowered"),
                type = Dependency.Type.API,
                modules = listOf(Module.CORE, Module.BUKKIT)
            ),
            Dependency(
                group = "org.spongepowered",
                artifact = "configurate-extra-kotlin",
                version = "4.1.2",
                relocation = Relocation("org.spongepowered", "org.sayandev.stickynote.lib.spongepowered"),
                type = Dependency.Type.API,
                modules = listOf(Module.CORE, Module.BUKKIT, Module.PAPER)
            ),
        )
    ),
    Repository(
        id = "spigotmc",
        repos = listOf("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"),
        dependencies = listOf(
            Dependency(
                group = "org.spigotmc",
                artifact = "spigot-api",
                version = "1.20.5-R0.1-SNAPSHOT",
                relocation = null,
                type = Dependency.Type.COMPILE_ONLY,
                modules = listOf(Module.BUKKIT, Module.PAPER)
            ),
        )
    ),
    Repository(
        id = "cloud",
        repos = listOf("https://oss.sonatype.org/content/repositories/snapshots"),
        dependencies = listOf(
            Dependency(
                group = "org.incendo",
                artifact = "cloud-core",
                version = "2.0.0-beta.5",
                relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
                type = Dependency.Type.API,
                modules = listOf(Module.CORE, Module.BUKKIT, Module.PAPER)
            ),
            Dependency(
                group = "org.incendo",
                artifact = "cloud-paper",
                version = "2.0.0-beta.5",
                relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
                type = Dependency.Type.API,
                modules = listOf(Module.BUKKIT, Module.PAPER)
            ),
            Dependency(
                group = "org.incendo",
                artifact = "cloud-minecraft-extras",
                version = "2.0.0-beta.5",
                relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
                type = Dependency.Type.API,
                modules = listOf(Module.BUKKIT, Module.PAPER)
            ),
            Dependency(
                group = "org.incendo",
                artifact = "cloud-kotlin-extensions",
                version = "2.0.0-beta.5",
                relocation = Relocation("org.incendo", "org.sayandev.stickynote.lib.incendo"),
                type = Dependency.Type.API,
                modules = listOf(Module.BUKKIT, Module.PAPER)
            ),
        )
    ),
    Repository(
        id = "inventoryframework",
        repos = emptyList(),
        dependencies = listOf(
            Dependency(
                group = "com.github.stefvanschie.inventoryframework",
                artifact = "IF",
                version = "0.10.13",
                relocation = Relocation("com.github.stefvanschie.inventoryframework", "org.sayandev.stickynote.lib.inventoryframework"),
                type = Dependency.Type.API,
                modules = listOf(Module.BUKKIT, Module.PAPER)
            ),
        )
    ),
    Repository(
        id = "central",
        repos = emptyList(),
        dependencies = listOf(
            Dependency(
                group = "com.github.cryptomorin",
                artifact = "XSeries",
                version = "10.0.0",
                relocation = Relocation("com.cryptomorin.xseries", "org.sayandev.stickynote.lib.xseries"),
                type = Dependency.Type.API,
                modules = listOf(Module.BUKKIT, Module.PAPER)
            ),
            Dependency(
                group = "net.kyori",
                artifact = "adventure-api",
                version = "4.17.0",
                relocation = Relocation("net.kyori", "org.sayandev.stickynote.lib.kyori"),
                type = Dependency.Type.API,
                modules = listOf(Module.CORE, Module.BUKKIT)
            ),
            Dependency(
                group = "net.kyori",
                artifact = "adventure-text-minimessage",
                version = "4.17.0",
                relocation = Relocation("net.kyori", "org.sayandev.stickynote.lib.kyori"),
                type = Dependency.Type.API,
                modules = listOf(Module.CORE, Module.BUKKIT)
            ),
            Dependency(
                group = "net.kyori",
                artifact = "adventure-platform-bukkit",
                version = "4.3.2",
                relocation = Relocation("net.kyori", "org.sayandev.stickynote.lib.kyori"),
                type = Dependency.Type.API,
                modules = listOf(Module.BUKKIT)
            ),
            Dependency(
                group = "com.mysql",
                artifact = "mysql-connector-j",
                version = "8.4.0",
                relocation = Relocation("com.mysql", "org.sayandev.stickynote.lib.mysql"),
                type = Dependency.Type.API,
                modules = listOf(/*Module.CORE, Module.BUKKIT*/)
            ),
            Dependency(
                group = "org.reflections",
                artifact = "reflections",
                version = "0.10.2",
                relocation = Relocation("org.reflections", "org.sayandev.stickynote.lib.reflections"),
                type = Dependency.Type.API,
                modules = listOf(Module.CORE, Module.BUKKIT, Module.PAPER)
            ),
            Dependency(
                group = "com.zaxxer",
                artifact = "HikariCP",
                version = "5.1.0",
                relocation = Relocation("com.zaxxer", "org.sayandev.stickynote.lib.zaxxer"),
                type = Dependency.Type.API,
                modules = listOf(Module.CORE, Module.BUKKIT, Module.PAPER)
            ),
            Dependency(
                group = "com.google.guava",
                artifact = "guava",
                version = "31.1-jre",
                relocation = Relocation("com.google.guava", "org.sayandev.stickynote.lib.guava"),
                type = Dependency.Type.COMPILE_ONLY_API,
                modules = listOf(Module.CORE, Module.BUKKIT, Module.PAPER)
            ),
            Dependency(
                group = "com.google.guava",
                artifact = "guava",
                version = "31.1-jre",
                relocation = Relocation("com.google.guava", "org.sayandev.stickynote.lib.guava"),
                type = Dependency.Type.TEST_IMPLEMENTATION,
                modules = listOf(Module.CORE, Module.BUKKIT, Module.PAPER)
            ),
            Dependency(
                group = "com.google.code.gson",
                artifact = "gson",
                version = "2.10.1",
                relocation = Relocation("com.google.gson", "org.sayandev.stickynote.lib.gson"),
                type = Dependency.Type.API,
                modules = listOf(Module.CORE, Module.BUKKIT)
            ),
        )
    ),
    /*Repository(
        id = "sonatype",
        repos = listOf("https://s01.oss.sonatype.org/content/repositories/snapshots/"),
        dependencies = listOf(
            Dependency(
                group = "com.alessiodp.libby",
                artifact = "libby-bukkit",
                version = "2.0.0-SNAPSHOT",
                relocation = Relocation("com.alessiodp.libby", "org.sayandev.stickynote.lib.libby"),
                type = Dependency.Type.API,
                modules = listOf(Module.BUKKIT, Module.PAPER),
                shadeMethod = Dependency.ShadeMethod.FORCE
            ),
        )
    ),*/
)