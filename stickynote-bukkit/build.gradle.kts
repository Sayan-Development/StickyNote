repositories {
    // SpigotAPI
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    // cloud
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    // libby
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnlyApi("org.spigotmc:spigot-api:1.20.5-R0.1-SNAPSHOT")

    api("net.kyori:adventure-platform-bukkit:4.3.2")

    api("org.incendo:cloud-core:2.0.0-SNAPSHOT")
    api("org.incendo:cloud-paper:2.0.0-SNAPSHOT")
    api("org.incendo:cloud-minecraft-extras:2.0.0-SNAPSHOT")
    api("org.incendo:cloud-kotlin-extensions:2.0.0-SNAPSHOT")

    api("com.github.stefvanschie.inventoryframework:IF:0.10.13")

    api("com.github.cryptomorin:XSeries:9.10.0") { isTransitive = false }

    api("org.reflections:reflections:0.10.2")

    compileOnlyApi(project(":stickynote-core"))
}

tasks {
    shadowJar {
        for (relocation in getRelocations().filter { !it.relocatePlatforms.contains(Platform.BUKKIT) }) {
            exclude(relocation.from.replace(".", "/") + "/**")
        }
        applyShadowRelocation(Platform.BUKKIT)
    }
}
