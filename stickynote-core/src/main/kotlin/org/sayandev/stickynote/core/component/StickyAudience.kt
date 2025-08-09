package org.sayandev.stickynote.core.component

interface StickyAudience {

    fun sendMessage(component: StickyComponent)

    fun sendActionbar(component: StickyComponent)

    fun sendTitle(component: StickyComponent)

    fun openBook(
        title: StickyComponent
    )

}