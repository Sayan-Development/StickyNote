package org.sayandev.stickynote.core.component

import org.sayandev.sayanventure.adventure.text.Component

interface StickyComponent {
    val content: String
    val tags: List<StickyTag>

    fun sayanComponent(): Component

    fun adventureComponent(): net.kyori.adventure.text.Component
}