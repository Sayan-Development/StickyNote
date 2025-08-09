package org.sayandev.stickynote.core.component

import org.sayandev.stickynote.core.component.modes.StickyComponentMiniMessage

class Test {
    init {
        val content = "<rainbow>Hello, World!"
        val stickyComponent = StickyComponentMiniMessage(content, listOf(
            StickyTag("rainbow", "color")
        ))
    }
}