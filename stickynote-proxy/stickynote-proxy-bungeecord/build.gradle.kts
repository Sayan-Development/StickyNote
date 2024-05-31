import org.sayandev.*
import org.sayandev.applyShadowRelocation
import org.sayandev.applyPlugins

repositories {
    applyRepositories(Module.BUNGEECORD)
}

dependencies {
    applyDependencies(Module.BUNGEECORD)

    compileOnlyApi(project(":stickynote-core"))
    compileOnlyApi(project(":stickynote-proxy"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.BUNGEECORD)
    }
}
