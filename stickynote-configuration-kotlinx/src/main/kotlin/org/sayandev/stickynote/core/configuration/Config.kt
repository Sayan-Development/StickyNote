package org.sayandev.stickynote.core.configuration

import com.charleskorn.kaml.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.overwriteWith
import java.io.File

object Config {

    @PublishedApi
    @Volatile
    internal var serializersModule: SerializersModule = EmptySerializersModule()

    @JvmStatic
    val yaml: Yaml
        get() = Yaml(
            serializersModule,
            YamlConfiguration(
                strictMode = false,
                yamlNamingStrategy = YamlNamingStrategy.KebabCase,
                encodeDefaults = true,
                decodeEnumCaseInsensitive = true,
                polymorphismStyle = PolymorphismStyle.Property
            )
        )

    inline fun <reified T : Any> registerSerializer(serializer: KSerializer<T>) {
        registerSerializersModule(SerializersModule {
            contextual(T::class, serializer)
        })
    }

    @JvmStatic
    fun registerSerializersModule(serializer: SerializersModule) {
        serializersModule = serializersModule.overwriteWith(serializer)
    }

    inline fun <reified T : Any> unregisterSerializer() {
        serializersModule = EmptySerializersModule()
    }

    inline fun <reified T> save(file: File, instance: T, yaml: Yaml = this.yaml) {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeText(yaml.encodeToString(instance))
    }

    @JvmStatic
    inline fun <reified T> fromFile(file: File): T? {
        return fromFile(file, EmptySerializersModule())
    }

    @JvmStatic
    inline fun <reified T> fromFile(
        file: File,
        serializers: SerializersModule,
        updateWithDefaults: Boolean = true
    ): T? {
        if (!file.exists()) return null
        return try {
            val serializerYaml = Yaml(serializers.overwriteWith(serializersModule), yaml.configuration)
            val result = serializerYaml.decodeFromString<T>(file.readText())

            if (updateWithDefaults) {
                save(file, result, serializerYaml)
            }

            result
        } catch (_: EmptyYamlDocumentException) {
            null
        }
    }
}
