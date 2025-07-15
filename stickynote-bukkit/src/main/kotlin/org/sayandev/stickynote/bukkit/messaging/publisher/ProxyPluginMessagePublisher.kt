package org.sayandev.stickynote.bukkit.messaging.publisher

import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.SimpleConnectionMeta

abstract class ProxyPluginMessagePublisher<P : Any, R : Any>(
    messageMeta: MessageMeta<P, R>,
    connectionMeta: SimpleConnectionMeta,
): PluginMessagePublisher<P, R>(
    messageMeta,
    connectionMeta,
    false
) {
    override fun handle(payload: P): R? {
        return null
    }
}