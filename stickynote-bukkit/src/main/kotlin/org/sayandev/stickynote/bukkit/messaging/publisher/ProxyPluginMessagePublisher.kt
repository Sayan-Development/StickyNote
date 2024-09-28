package org.sayandev.stickynote.bukkit.messaging.publisher

import kotlin.reflect.KClass

abstract class ProxyPluginMessagePublisher<P: Any, S: Any>(
    namespace: String,
    name: String,
    payloadClass: KClass<P>,
    resultClass: KClass<S>,
): PluginMessagePublisher<P, S>(
    namespace,
    name,
    payloadClass,
    resultClass,
    true
) {
    override fun handle(payload: P): S? {
        return null
    }
}