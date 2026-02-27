dependencies {
    api(libs.adventure.api)
    api(libs.adventure.text.minimessage)
    api(libs.adventure.text.serializer.gson)
    api(libs.reflections)
    api(libs.configurate.extra.kotlin)
    api(libs.gson)
    api(libs.kotlin.reflect)
    api(libs.kotlinx.coroutines)

    compileOnlyApi(libs.guava)
    compileOnlyApi(libs.netty.all)
}
