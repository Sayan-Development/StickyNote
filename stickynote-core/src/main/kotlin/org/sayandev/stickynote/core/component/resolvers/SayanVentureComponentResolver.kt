package org.sayandev.stickynote.core.component.resolvers

import org.sayandev.sayanventure.adventure.text.Component
import org.sayandev.sayanventure.adventure.text.minimessage.MiniMessage
import org.sayandev.stickynote.core.component.ComponentResolver

object SayanVentureComponentResolver : ComponentResolver<Component> {
    override fun resolve(content: String): Component {
        return MiniMessage.miniMessage().deserialize(content)
    }
}