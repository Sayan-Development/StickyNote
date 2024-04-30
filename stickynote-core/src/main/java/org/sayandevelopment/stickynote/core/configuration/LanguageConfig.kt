package org.sayandevelopment.stickynote.core.configuration

import org.spongepowered.configurate.CommentedConfigurationNode
import java.io.File

abstract class LanguageConfig(
    val directory: File,
    val languageId: String,
) : Config(
    File(directory, "$languageId.yml")
) {

    val sections = mutableMapOf<Any, List<String>>()

    inline fun <reified T : Any> add(data: T, path: List<String>) {
        sections[data] = path
        config.node(path).set(data)
    }

    inline fun <reified T : Any> add(data: T, vararg path: String) {
        sections[data] = path.toList()
    }

    inline fun <reified T> get(): T {
        return getSectionConfig<T>().get(T::class.java)!!
    }

    inline fun <reified T> getSectionConfig(): CommentedConfigurationNode {
        val path = sections.entries.find { it.key.javaClass == T::class.java }
        require(path != null) { "Couldn't find section for class `${T::class.java.name}`" }

        return config.node(path)
    }

    override fun save() {
        for ((data, path) in sections) {
            config.node(path).set(data)
        }
        super.save()
    }

}