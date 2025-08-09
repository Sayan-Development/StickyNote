package org.sayandev.stickynote.core.component.modes

import org.sayandev.sayanventure.adventure.text.Component
import org.sayandev.sayanventure.adventure.text.serializer.gson.GsonComponentSerializer
import org.sayandev.stickynote.core.component.StickyComponent
import org.sayandev.stickynote.core.component.StickyTag

class StickyComponentGson(
    override val content: String,
    override val tags: List<StickyTag>
) : StickyComponent {
    override fun sayanComponent(): Component {
        return GsonComponentSerializer.gson().deserialize(content.let {
            for (tag in tags) {
                it.replace("<${tag.key}>", tag.value)
            }
            it
        })
    }

    override fun adventureComponent(): net.kyori.adventure.text.Component {
        return net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(content.let {
            for (tag in tags) {
                it.replace("<${tag.key}>", tag.value)
            }
            it
        })
    }
}