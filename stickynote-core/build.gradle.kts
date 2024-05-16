repositories {
    // libby
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
    implementation("org.spongepowered:configurate-extra-kotlin:4.2.0-SNAPSHOT")

    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")

    implementation("com.mysql:mysql-connector-j:8.4.0")

    implementation("org.reflections:reflections:0.10.2")

    /*api("org.apache.maven:maven-resolver-provider:3.9.6")
    api("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.18")
    api("org.apache.maven.resolver:maven-resolver-transport-http:1.9.18")
    api("com.alessiodp.libby:libby-bukkit:2.0.0-SNAPSHOT")*/

    implementation("com.zaxxer:HikariCP:5.1.0")

    compileOnlyApi("com.google.guava:guava:31.1-jre")
    testImplementation("com.google.guava:guava:31.1-jre")
}

tasks {
    shadowJar {
        applyShadowRelocation(Platform.CORE)
    }
}