package org.sayandev.stickynote.core.configuration

import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.nio.file.Path
import java.util.UUID

abstract class ConfigurateConfig(
    @Transient val directory: File,
    @Transient val name: String,
    @Transient val builder: YamlConfigurationLoader.Builder,
    @Transient val serializers: TypeSerializerCollection = ConfigurateConfiguration.generateOptions(null).serializers()
) {

    init {
        ConfigurateConfiguration.register(this)
    }

    @Transient val file = File(directory, name)

    constructor(directory: File, name: String, serializers: TypeSerializerCollection?) : this(
        directory,
        name,
        ConfigurateConfiguration.getConfigBuilder(File(directory, name), serializers),
        ConfigurateConfiguration.generateOptions(serializers).serializers()
    )

    constructor(directory: File, name: String) : this(directory, name, null)
    constructor(directoryPath: Path, name: String) : this(directoryPath.toFile(), name, null)

    @Transient var yaml = builder.defaultOptions(ConfigurateConfiguration.generateOptions(serializers)).build()
    @Transient var config = yaml.load(ConfigurateConfiguration.generateOptions(serializers))

    open fun save() {
        createFile()
        update()
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

    open fun update() {
        yaml = builder.defaultOptions(ConfigurateConfiguration.generateOptions(serializers)).build()
        config = yaml.load(ConfigurateConfiguration.generateOptions(serializers))
    }
}

object ConfigurateConfiguration {
    val registeredConfigurations = mutableMapOf<String, ConfigurateConfig>()

    fun register(config: ConfigurateConfig) {
        registeredConfigurations[config.name] = config
    }

    fun unregister(config: ConfigurateConfig) {
        registeredConfigurations.remove(config.name)
    }

    @JvmStatic
    fun generateOptions(serializers: TypeSerializerCollection?): ConfigurationOptions {
        return ConfigurationOptions.defaults()
            .shouldCopyDefaults(true)
            .serializers { builder ->
                builder.registerAnnotatedObjects(objectMapperFactory())
                builder.register(Enum::class.java, EnumSerializer())
                builder.register(UUID::class.java, UUIDSerializer())
                if (serializers != null) {
                    builder.registerAll(serializers)
                }
                builder.registerAll(TypeSerializerCollection.defaults())
            }
    }

    @JvmStatic
    fun getConfigBuilder(file: File, serializers: TypeSerializerCollection?): YamlConfigurationLoader.Builder {
        return YamlConfigurationLoader.builder()
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .defaultOptions(generateOptions(serializers))
            .file(file)
    }

    @JvmStatic
    inline fun <reified T> fromConfig(file: File, serializers: TypeSerializerCollection?): T? {
        if (!file.exists()) return null
        val yaml = getConfigBuilder(file, serializers).build()
        val config = yaml.load(generateOptions(serializers))
        val result = config.get(T::class.java)
        config.set(result)
        yaml.save(config)
        return result
    }

    @JvmStatic
    fun <T> fromConfigWithClass(file: File, type: Class<*>, serializers: TypeSerializerCollection?): T? {
        if (!file.exists()) return null
        return getConfigBuilder(file, serializers).build().load().get(type) as T
    }

    @JvmStatic
    fun getConfigFromFile(file: File): CommentedConfigurationNode? {
        if (!file.exists()) return null
        return getConfigBuilder(file, null).build().load()
    }

    @JvmStatic
    fun getConfigFromFile(file: File, serializers: TypeSerializerCollection?): CommentedConfigurationNode? {
        if (!file.exists()) return null
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
