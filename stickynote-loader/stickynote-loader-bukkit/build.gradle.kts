import org.sayandev.Module
import org.sayandev.applyDependencies
import org.sayandev.applyRepositories
import org.sayandev.applyShadowRelocation

repositories {
    applyRepositories(Module.LOADER_BUKKIT)
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    applyDependencies(Module.LOADER_BUKKIT)
    compileOnly(project(":stickynote-bukkit"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.LOADER_BUKKIT)
    }
}