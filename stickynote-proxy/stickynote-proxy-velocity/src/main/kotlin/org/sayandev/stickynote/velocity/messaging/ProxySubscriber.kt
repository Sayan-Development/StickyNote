package org.sayandev.stickynote.velocity.messaging

import org.sayandev.stickynote.core.messaging.subscriber.Subscriber

abstract class ProxySubscriber<P, S>(channel: String) : Subscriber<P, S>(channel) {
    init {
        PluginMessageSubscribeListener<Unit, Int>("stickynotetest", "count", Unit::class.java)
    }
}