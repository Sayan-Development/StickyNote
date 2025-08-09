package org.sayandev.stickynote.core.component

interface ComponentResolver<C> {
    fun resolve(content: String): C
}