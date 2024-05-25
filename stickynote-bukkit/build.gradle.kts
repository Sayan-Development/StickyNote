import org.sayandev.*
import org.sayandev.applyShadowRelocation

generateRepositoriesClass(Module.BUKKIT)

repositories {
    applyRepositories(Module.BUKKIT)
}

dependencies {
    applyDependencies(Module.BUKKIT)

    compileOnlyApi(project(":stickynote-core"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.BUKKIT)
    }
}