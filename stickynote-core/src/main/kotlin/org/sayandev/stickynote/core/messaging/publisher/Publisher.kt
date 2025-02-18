package org.sayandev.stickynote.core.messaging.publisher

import kotlinx.coroutines.CompletableDeferred
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.publisher.websocket.WebSocketPublisher
import org.sayandev.stickynote.core.messaging.publisher.websocket.WebSocketPublisherMeta
import java.util.*
import java.util.logging.Logger

abstract class Publisher<M: PublisherMeta, P : Any, S : Any>(
    val messageMeta: MessageMeta<P, S>,
    val publisherMeta: M,
    val logger: Logger,
) {

    val payloads: MutableMap<UUID, CompletableDeferred<S>> = mutableMapOf()

    open suspend fun publish(payloadWrapper: PayloadWrapper<P>): CompletableDeferred<S> {
        val deferred = CompletableDeferred<S>()
        payloads[payloadWrapper.uniqueId] = deferred

        return deferred
    }

    open fun handle(payload: P): S? {
        return null
    }

    fun isSource(uniqueId: UUID): Boolean {
        return HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.contains(uniqueId)
    }

    fun register() {
        register(this)
    }

    fun unregister() {
        unregister(this)
    }

    companion object {
        val HANDLER_LIST = mutableListOf<Publisher<out PublisherMeta, *, *>>()

        fun <M : PublisherMeta, P : Any, S : Any> register(publisher: Publisher<M, P, S>) {
            require(!HANDLER_LIST.contains(publisher)) { "Publisher with id ${publisher.messageMeta.id()} is already registered" }
            HANDLER_LIST.add(publisher)
        }

        fun <M : PublisherMeta, P : Any, S : Any> unregister(publisher: Publisher<M, P, S>) {
            HANDLER_LIST.remove(publisher)
        }

        inline fun <reified T : Publisher<M, P, S>, reified M : PublisherMeta, reified P : Any, reified S : Any> create(messageMeta: MessageMeta<P, S>, publisherMeta: M, logger: Logger): T {
            return when (T::class) {
                WebSocketPublisher::class -> {
                    WebSocketPublisher.create(messageMeta, publisherMeta as WebSocketPublisherMeta, logger) as T
                }
                else -> {
                    throw IllegalArgumentException("Publisher type not supported")
                }
            }
        }

        inline fun <reified T : Publisher<M, P, S>, reified M : PublisherMeta, reified P : Any, reified S : Any> createAndRegister(messageMeta: MessageMeta<P, S>, publisherMeta: M, logger: Logger): T {
            return when (T::class) {
                WebSocketPublisher::class -> {
                    WebSocketPublisher.create(messageMeta, publisherMeta as WebSocketPublisherMeta, logger) as T
                }
                else -> {
                    throw IllegalArgumentException("Publisher type not supported")
                }
            }.also { it.register() }
        }
    }

}