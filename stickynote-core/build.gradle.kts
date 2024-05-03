dependencies {
    implementation("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
    implementation("org.spongepowered:configurate-extra-kotlin:4.2.0-SNAPSHOT")

    implementation("net.kyori:adventure-api:4.16.0")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")

    api("com.mysql:mysql-connector-j:8.4.0")

    implementation("org.apache.maven:maven-resolver-provider:3.9.6")
    implementation("org.apache.maven.resolver:maven-resolver-connector-basic:1.9.18")
    implementation("org.apache.maven.resolver:maven-resolver-transport-http:1.9.18")

    implementation("com.zaxxer:HikariCP:5.1.0")

    compileOnlyApi("com.google.guava:guava:31.1-jre")
    testImplementation("com.google.guava:guava:31.1-jre")
}

tasks {
    shadowJar {
        applyShadowRelocation(Platform.CORE)
    }
}