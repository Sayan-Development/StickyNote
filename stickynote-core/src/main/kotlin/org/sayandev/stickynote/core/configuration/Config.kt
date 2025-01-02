package org.sayandev.stickynote.core.configuration

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.nio.file.Path

abstract class Config(
    @Transient val directory: File,
    @Transient val name: String,
    @Transient val builder: YamlConfigurationLoader.Builder,
    @Transient val serializers: TypeSerializerCollection = generateOptions(null).serializers()
) {

    @Transient val file = File(directory, name)

    constructor(directory: File, name: String, serializers: TypeSerializerCollection?) : this(
        directory,
        name,
        getConfigBuilder(File(directory, name), serializers),
        generateOptions(serializers).serializers()
    )
    constructor(directory: File, name: String) : this(directory, name, null)
    constructor(directoryPath: Path, name: String) : this(directoryPath.toFile(), name, null)

    @Transient var yaml = builder.build()
    @Transient var config = yaml.load(generateOptions(serializers))

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
        yaml = builder.defaultOptions(generateOptions(serializers)).build()
        config = yaml.load(generateOptions(serializers))
        config.set(config)
    }

    companion object {
        @JvmStatic
        fun generateOptions(serializers: TypeSerializerCollection?): ConfigurationOptions {
            return ConfigurationOptions.defaults().shouldCopyDefaults(true)
            .serializers { builder ->
                builder.registerAnnotatedObjects(objectMapperFactory())
                if (serializers != null) {
                    builder.registerAll(serializers)
                }
            }
        }

        @JvmStatic
        fun getConfigBuilder(file: File, serializers: TypeSerializerCollection?): YamlConfigurationLoader.Builder {
            val yaml = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.BLOCK)
                .defaultOptions(generateOptions(serializers))
                .file(file)
            return yaml
        }

        @JvmStatic
        inline fun <reified T> fromConfig(file: File, serializers: TypeSerializerCollection?): T? {
            return getConfigBuilder(file, serializers).build().load().get(T::class.java)
        }

        @JvmStatic
        fun <T> fromConfigWithClass(file: File, type: Class<*>, serializers: TypeSerializerCollection?): T? {
            return getConfigBuilder(file, serializers).build().load().get(type) as T
        }

        @JvmStatic
        fun getConfigFromFile(file: File): CommentedConfigurationNode? {
            return getConfigBuilder(file, null).build().load()
        }

        @JvmStatic
        fun getConfigFromFile(file: File, serializers: TypeSerializerCollection?): CommentedConfigurationNode? {
            return getConfigBuilder(file, serializers).build().load()
        }

        @JvmStatic
        fun <T> fromConfigWithClass(file: File, type: Class<*>): T? {
            if (!file.exists()) return null
            return fromConfigWithClass(file, type, null) as T?
        }

        @JvmStatic
        inline fun <reified T> fromConfig(file: File): T? {
            if (!file.exists()) return null
            return fromConfig(file, null)
        }
    }

}