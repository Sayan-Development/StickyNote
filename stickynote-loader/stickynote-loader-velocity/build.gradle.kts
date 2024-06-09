import org.sayandev.Module
import org.sayandev.applyDependencies
import org.sayandev.applyRepositories
import org.sayandev.applyShadowRelocation

repositories {
    applyRepositories(Module.LOADER_VELOCITY)
}

dependencies {
    applyDependencies(Module.LOADER_VELOCITY)
    compileOnly(project(":stickynote-proxy:stickynote-proxy-velocity"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.LOADER_VELOCITY)
    }
}