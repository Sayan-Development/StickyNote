package org.sayandev.stickynote.core.component.modes

import org.sayandev.sayanventure.adventure.text.Component
import org.sayandev.sayanventure.adventure.text.minimessage.MiniMessage
import org.sayandev.stickynote.core.component.resolvers.AdventureComponentResolver
import org.sayandev.stickynote.core.component.resolvers.SayanVentureComponentResolver
import org.sayandev.stickynote.core.component.StickyComponent
import org.sayandev.stickynote.core.component.StickyTag

class StickyComponentMiniMessage(
    override val content: String,
    override val tags: List<StickyTag>
) : StickyComponent {
    override fun sayanComponent(): Component {
        return MiniMessage.miniMessage().deserialize(content, *tags.map { it.toSayanPlaceholder(SayanVentureComponentResolver) }.toTypedArray())
    }

    override fun adventureComponent(): net.kyori.adventure.text.Component {
        return net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(content, *tags.map { it.toAdventurePlaceholder(AdventureComponentResolver) }.toTypedArray())
    }
}