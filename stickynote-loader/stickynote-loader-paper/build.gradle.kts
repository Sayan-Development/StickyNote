dependencies {
    compileOnly(libs.folia)

    compileOnly(libs.libby.paper)

    api(project(":stickynote-loader:stickynote-loader-common"))

    compileOnly(project(":stickynote-paper"))
}

tasks {
    java {
        disableAutoTargetJvm()
    }
}
