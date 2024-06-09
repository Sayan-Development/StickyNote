import org.sayandev.*
import org.sayandev.applyShadowRelocation

repositories {
    applyRepositories(Module.BUKKIT)
}

dependencies {
    applyDependencies(Module.BUKKIT)

    compileOnly(project(":stickynote-core"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.BUKKIT)
    }
}