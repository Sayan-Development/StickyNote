import org.sayandev.Module
import org.sayandev.applyDependencies
import org.sayandev.applyRepositories
import org.sayandev.applyShadowRelocation

repositories {
    applyRepositories(Module.LOADER_BUNGEECORD)
}

dependencies {
    applyDependencies(Module.LOADER_BUNGEECORD)
    compileOnly(project(":stickynote-proxy:stickynote-proxy-bungeecord"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.LOADER_BUNGEECORD)
    }
}