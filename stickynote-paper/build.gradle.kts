repositories {
    // Takenaka
    maven("https://repo.screamingsandals.org/public")

    // SpigotAPI
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    // cloud
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    api(project(":stickynote-bukkit"))
}