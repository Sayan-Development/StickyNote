package org.sayandev.stickynote.core.component.resolvers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.sayandev.stickynote.core.component.ComponentResolver

object AdventureComponentResolver : ComponentResolver<Component> {
    override fun resolve(content: String): Component {
        return MiniMessage.miniMessage().deserialize(content)
    }
}