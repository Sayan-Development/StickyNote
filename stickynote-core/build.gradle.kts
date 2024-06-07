import org.sayandev.Module
import org.sayandev.applyDependencies
import org.sayandev.applyRepositories
import org.sayandev.applyShadowRelocation

repositories {
    applyRepositories(Module.CORE)
}

dependencies {
    applyDependencies(Module.CORE)
    implementation(kotlin("stdlib", version = "2.0.0"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.CORE)
    }
}