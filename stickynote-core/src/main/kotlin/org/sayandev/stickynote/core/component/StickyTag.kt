package org.sayandev.stickynote.core.component

import org.sayandev.sayanventure.adventure.text.minimessage.tag.resolver.Placeholder
import org.sayandev.sayanventure.adventure.text.minimessage.tag.resolver.TagResolver
import org.sayandev.stickynote.core.component.resolvers.AdventureComponentResolver
import org.sayandev.stickynote.core.component.resolvers.SayanVentureComponentResolver

data class StickyTag(
    val key: String,
    val value: String,
    val mode: StickyTagMode = StickyTagMode.UNPARSED
) {
    fun toSayanPlaceholder(): TagResolver.Single {
        return when (mode) {
            StickyTagMode.UNPARSED -> Placeholder.unparsed(key, value)
            StickyTagMode.PARSED -> Placeholder.parsed(key, value)
            StickyTagMode.COMPONENT -> Placeholder.component(key, SayanVentureComponentResolver.resolve(value))
        }
    }

    fun toAdventurePlaceholder(): net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single {
        return when (mode) {
            StickyTagMode.UNPARSED -> net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed(key, value)
            StickyTagMode.PARSED -> net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.parsed(key, value)
            StickyTagMode.COMPONENT -> net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.component(key, AdventureComponentResolver.resolve(value))
        }
    }
}