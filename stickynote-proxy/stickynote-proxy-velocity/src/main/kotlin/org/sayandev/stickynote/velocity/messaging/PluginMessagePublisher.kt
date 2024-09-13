package org.sayandev.stickynote.velocity.messaging

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import org.sayandev.stickynote.core.messaging.publisher.Publisher

abstract class PluginMessagePublisher: Publisher<String, Boolean>(
    "plugin"
) {



}