package org.sayandev.stickynote.velocity.messaging

import org.sayandev.stickynote.core.messaging.subscriber.Subscriber

abstract class ProxySubscriber<P, S>(namespace: String, name: String) : Subscriber<P, S>(namespace, name) {
    init {
        PluginMessageSubscribeListener<Unit, Int>(namespace, name, Unit::class.java)
    }
}