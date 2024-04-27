package org.sayandevelopment.core.configuration

import org.spongepowered.configurate.ScopedConfigurationNode
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.nio.file.Path

abstract class Config(
    val file: File,
    val builder: YamlConfigurationLoader.Builder
) {

    constructor(file: File) : this(
        file,
        YamlConfigurationLoader.builder()
            .nodeStyle(NodeStyle.BLOCK)
            .defaultOptions { options ->
                options.serializers { builder ->
                    builder.registerAnnotatedObjects(objectMapperFactory())
                }
            }
            .file(file)
    )

    constructor(path: Path) : this(path.toFile())

    @Transient var yaml = builder.build()
    @Transient var config = yaml.load()

    init {
        reload()
    }

    open fun save() {
        config.set(this)
        yaml.save(config)
    }

    open fun reload() {
        yaml = builder.build()
        config = yaml.load()
    }

}