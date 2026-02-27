dependencies {
    api(libs.mysql.connector)
    api(libs.hikari)
    api(libs.mariadb)
    api(libs.sqlite.jdbc)

    api(libs.exposed.core)
    api(libs.exposed.jdbc)
    api(libs.exposed.dao)
    api(libs.exposed.kotlin.datetime)

    compileOnlyApi(libs.guava)

    api(project(":stickynote-core"))
}
