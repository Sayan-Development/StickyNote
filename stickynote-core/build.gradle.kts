dependencies {
    api(libs.snakeyaml)
    api(libs.configurate.yaml)
    api(libs.configurate.extra.kotlin)
    api(libs.cloud.core)
    api(libs.cloud.kotlin.extensions)
    api(libs.adventure.api)
    api(libs.adventure.text.minimessage)
    api(libs.adventure.text.serializer.gson)
    api(libs.mysql.connector)
    api(libs.jedis)
    api(libs.reflections)
    api(libs.hikari)
    api(libs.gson)
    api(libs.kotlin.reflect)

    compileOnlyApi(libs.guava)
    compileOnlyApi(libs.netty.all)
}