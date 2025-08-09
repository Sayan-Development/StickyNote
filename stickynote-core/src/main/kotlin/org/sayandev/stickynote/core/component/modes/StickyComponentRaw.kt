package org.sayandev.stickynote.core.component.modes

import org.sayandev.sayanventure.adventure.text.Component
import org.sayandev.stickynote.core.component.StickyComponent
import org.sayandev.stickynote.core.component.StickyTag

class StickyComponentRaw(
    override val content: String,
    override val tags: List<StickyTag>
) : StickyComponent {
    override fun sayanComponent(): Component {
        return Component.text(
            content.let {
                for (tag in tags) {
                    it.replace("<${tag.key}>", tag.value)
                }
                it
            }
        )
    }

    override fun adventureComponent(): net.kyori.adventure.text.Component {
        return net.kyori.adventure.text.Component.text(
            content.apply {
                for (tag in tags) {
                    this.replace("<${tag.key}>", tag.value)
                }
                this
            }
        )
    }
}