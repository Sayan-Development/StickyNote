package org.sayandev.stickynote.core.configuration

import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.serialize.TypeSerializer
import org.spongepowered.configurate.util.MapFactories
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.nio.file.Path

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
                options.mapFactory(MapFactories.sortedNatural())
            }
            .file(File(directory, name)),
    )
    constructor(directoryPath: Path, name: String) : this(directoryPath.toFile(), name)

    @Transient var yaml = builder.build()
    @Transient var config = yaml.load()

    fun load() {
        save()
        reload()
    }

    open fun save() {
        createFile()

        config.set(this)
        yaml.save(config)
    }

    fun createFile(): Boolean {
        if (!file.exists()) {
            directory.mkdirs()
            file.createNewFile()
            return true
        }
        return false
    }

    open fun reload() {
        yaml = builder.build()
        config = yaml.load(ConfigurationOptions.defaults().apply {
            shouldCopyDefaults(true)
        })
        config.set(config)
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