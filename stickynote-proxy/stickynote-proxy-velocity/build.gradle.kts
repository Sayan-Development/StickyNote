import org.sayandev.*
import org.sayandev.applyShadowRelocation
import org.sayandev.applyPlugins

plugins {
//    applyPlugins(Module.VELOCITY)
}

repositories {
    applyRepositories(Module.VELOCITY)
}

dependencies {
    applyDependencies(Module.VELOCITY)

    compileOnlyApi(project(":stickynote-core"))
    compileOnlyApi(project(":stickynote-proxy"))
}

tasks {
    shadowJar {
        applyShadowRelocation(Module.VELOCITY)
    }
}

/*
tasks {
    runVelocity {
        velocityVersion("3.3.0-SNAPSHOT")
    }
}*/
