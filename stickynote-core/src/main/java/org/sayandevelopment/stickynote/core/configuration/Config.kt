package org.sayandevelopment.stickynote.core.configuration

import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.serialize.TypeSerializer
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.nio.file.Path
import java.util.logging.Logger

abstract class Config(
    @Transient val directory: File,
    @Transient val name: String,
    @Transient val builder: YamlConfigurationLoader.Builder,
) {

    @Transient val file = File(directory, name)

    constructor(directory: File, name: String) : this(
        directory,
        name,
        YamlConfigurationLoader.builder()
            .nodeStyle(NodeStyle.BLOCK)
            .defaultOptions { options ->
                options.serializers { builder ->
                    builder.registerAnnotatedObjects(objectMapperFactory())
                }
            }
            .file(File(directory, name)),
    )
    constructor(directoryPath: Path, name: String) : this(directoryPath.toFile(), name)

    @Transient var yaml = builder.build()
    @Transient var config = yaml.load()

    init {
        load()
    }

    open fun load() {
        createFile()
        reload()
    }

    open fun save() {
        createFile()

        config.set(this)
        yaml.save(config)
    }

    fun createFile() {
        if (!file.exists()) {
            directory.mkdirs()
            file.createNewFile()
        }
    }

    open fun reload() {
        yaml = builder.build()
        config = yaml.load()
    }

    companion object {
        inline fun <reified T> fromConfig(file: File, vararg serializers: TypeSerializer<Any>): T? {
            val yaml = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .defaultOptions { options ->
                    options.serializers { builder ->
                        builder.registerAnnotatedObjects(objectMapperFactory())
                        for (serializer in serializers) {
                            builder.register(serializer::class.java, serializer)
                        }
                    }
                }
                .file(file)
                .build()
                .load()
            return yaml.get(T::class.java)
        }
    }

}