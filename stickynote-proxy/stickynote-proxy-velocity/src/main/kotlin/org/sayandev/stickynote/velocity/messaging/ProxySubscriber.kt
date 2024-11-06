package org.sayandev.stickynote.velocity.messaging

import org.sayandev.stickynote.core.messaging.subscriber.Subscriber

abstract class ProxySubscriber<P, S>(namespace: String, name: String, payloadClass: Class<P>) : Subscriber<P, S>(namespace, name) {
    init {
        PluginMessageSubscribeListener<P, S>(namespace, name, payloadClass)
    }
}