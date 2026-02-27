package org.sayandev.stickynote.core.configuration

import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.serialize.TypeSerializer
import java.lang.reflect.Type
import java.util.*

class UUIDSerializer : TypeSerializer<UUID> {
    override fun deserialize(type: Type, node: ConfigurationNode): UUID {
        return UUID.fromString(node.string)
    }

    override fun serialize(type: Type, obj: UUID?, node: ConfigurationNode) {
        node.set(obj!!.toString())
    }
}