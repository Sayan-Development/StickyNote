dependencies {
    api("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
    api("org.spongepowered:configurate-extra-kotlin:4.2.0-SNAPSHOT")

    api("net.kyori:adventure-api:4.16.0")
    api("net.kyori:adventure-text-minimessage:4.16.0")

    implementation("com.mysql:mysql-connector-j:8.4.0")

    api("com.zaxxer:HikariCP:5.1.0")

    compileOnlyApi("com.google.guava:guava:31.1-jre")
    testImplementation("com.google.guava:guava:31.1-jre")
}

tasks {
    shadowJar {
        applyShadowRelocation(Platform.CORE)
    }
}