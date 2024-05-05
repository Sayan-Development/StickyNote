dependencies {
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("org.spongepowered:configurate-extra-kotlin:4.1.2")

    implementation("net.kyori:adventure-api:4.16.0")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")

    compileOnly("com.google.guava:guava:31.1-jre")
    compileOnly(kotlin("reflect"))

    implementation("com.zaxxer:HikariCP:5.1.0")
}