package org.sayandev.stickynote.core.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import java.util.*
import java.util.logging.Logger

abstract class Publisher<P, S>(
    val logger: Logger,
    val namespace: String,
    val name: String,
) {

    val payloads: MutableMap<UUID, CompletableDeferred<S>> = mutableMapOf()

    fun id(): String {
        return "$namespace:$name"
    }

    fun publish(payloadWrapper: PayloadWrapper<P>): CompletableDeferred<S> {
        val deferred = CompletableDeferred<S>()
        payloads[payloadWrapper.uniqueId] = deferred

        return deferred
    }

    companion object {
        val HANDLER_LIST = mutableListOf<Publisher<*, *>>()

        fun <P, S> register(publisher: Publisher<P, S>) {
            require(!HANDLER_LIST.contains(publisher)) { "Publisher with id ${publisher.id()} is already registered" }
            HANDLER_LIST.add(publisher)
        }

        fun <P, S> unregister(publisher: Publisher<P, S>) {
            HANDLER_LIST.remove(publisher)
        }
    }

}