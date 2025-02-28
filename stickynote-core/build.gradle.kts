dependencies {
    api(libs.cloud.core)
    api(libs.cloud.kotlin.extensions)
//    api(libs.cloud.kotlin.coroutines)
    api(libs.adventure.api)
    api(libs.adventure.text.minimessage)
    api(libs.adventure.text.serializer.gson)
    api(libs.mysql.connector)
    api(libs.jedis)
    api(libs.reflections)
    api(libs.hikari)
    api(libs.gson)
    api(libs.kotlin.reflect)
    api(libs.kotlinx.coroutines)

    compileOnlyApi(libs.guava)
    compileOnlyApi(libs.netty.all)
}
