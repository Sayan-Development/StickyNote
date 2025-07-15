package org.sayandev.stickynote.core.messaging

import kotlin.reflect.KClass

data class MessageMeta<P : Any, S : Any>(
    val namespace: String,
    val name: String,
    val payloadType: KClass<P>,
    val resultType: KClass<S>,
) {
    fun id(): String {
        return "$namespace:$name"
    }

    companion object {
        inline fun <reified P : Any, reified S : Any> create(namespace: String, name: String): MessageMeta<P, S> {
            return MessageMeta(namespace, name, P::class, S::class)
        }
    }
}