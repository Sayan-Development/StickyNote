package org.sayandev.stickynote.core.configuration

import io.leangen.geantyref.GenericTypeReflector
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.SerializationException
import org.spongepowered.configurate.serialize.TypeSerializer
import org.spongepowered.configurate.util.EnumLookup
import java.lang.reflect.Type

class EnumSerializer : TypeSerializer<Enum<*>> {
    override fun deserialize(
        type: Type,
        node: ConfigurationNode
    ): Enum<*> {
        return EnumLookup.lookupEnum(GenericTypeReflector.erase(type).asSubclass(Enum::class.java), node.string) as? Enum<*> ?: let {
            throw SerializationException(type, "Invalid enum constant provided, expected a value of enum, got ${node.string}")
        }
    }

    override fun serialize(
        type: Type,
        obj: Enum<*>?,
        node: ConfigurationNode
    ) {
        node.set(obj!!.name)
    }
}