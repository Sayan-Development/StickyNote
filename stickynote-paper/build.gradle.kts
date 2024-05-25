import org.sayandev.Module
import org.sayandev.applyDependencies
import org.sayandev.applyRepositories
import org.sayandev.applyShadowRelocation

repositories {
    applyRepositories(Module.PAPER)
}

dependencies {
    applyDependencies(Module.PAPER)
    compileOnlyApi(project(":stickynote-bukkit"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.PAPER)
    }
}