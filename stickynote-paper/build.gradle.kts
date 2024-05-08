repositories {
    // Takenaka
    maven("https://repo.screamingsandals.org/public")

    // SpigotAPI
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    // cloud
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(project(":stickynote-bukkit")) { isTransitive = false }
}

tasks {
    shadowJar {
        for (relocation in getRelocations().filter { !it.relocatePlatforms.contains(Platform.PAPER) }) {
            exclude(relocation.to.replace(".", "/") + "/**")
            exclude(relocation.from.replace(".", "/") + "/**")
        }
        applyShadowRelocation(Platform.PAPER)
    }
}