package org.sayandev.stickynote.bukkit.messaging.publisher

abstract class ProxyPluginMessagePublisher<P, S>(
    namespace: String,
    name: String,
    payloadClass: Class<P>,
    resultClass: Class<S>,
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