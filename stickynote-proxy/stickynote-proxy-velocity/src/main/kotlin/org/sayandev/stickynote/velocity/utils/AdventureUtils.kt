package org.sayandev.stickynote.velocity.utils

import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

object AdventureUtils {

    @JvmStatic
    var miniMessage = MiniMessage.miniMessage()

    fun setTagResolver(vararg tagResolver: TagResolver) {
        miniMessage = MiniMessage.builder().tags(TagResolver.resolver(TagResolver.standard(), *tagResolver)).build()
    }

    @JvmStatic
    fun toComponent(content: String, vararg placeholder: TagResolver): Component {
        return miniMessage.deserialize(content, *placeholder)
    }

    fun String.component(vararg placeholder: TagResolver): Component {
        return toComponent(this, *placeholder)
    }



}