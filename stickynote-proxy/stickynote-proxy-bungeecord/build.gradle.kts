import org.sayandev.*
import org.sayandev.applyShadowRelocation
import org.sayandev.applyPlugins

repositories {
    applyRepositories(Module.BUNGEECORD)
}

dependencies {
    applyDependencies(Module.BUNGEECORD)

    compileOnly(project(":stickynote-core"))
    compileOnly(project(":stickynote-proxy"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.BUNGEECORD)
    }
}
