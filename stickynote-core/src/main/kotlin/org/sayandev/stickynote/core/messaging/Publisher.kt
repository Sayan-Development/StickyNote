package org.sayandev.stickynote.core.messaging

import kotlinx.coroutines.CompletableDeferred
import java.util.UUID
import java.util.logging.Logger

abstract class Publisher<M: ConnectionMeta, P : Any, R : Any>(
    val messageMeta: MessageMeta<P, R>,
    val connectionMeta: M,
    val logger: Logger,
) {

    val payloads: MutableMap<UUID, CompletableDeferred<R>> = mutableMapOf()

    open suspend fun publish(payloadWrapper: PayloadWrapper<P>): CompletableDeferred<R> {
        val deferred = CompletableDeferred<R>()

        if (!registered()) {
            logger.warning("A payload has published without being registered but the publisher is not registered: ${messageMeta}")
            return deferred
        }

        payloads[payloadWrapper.uniqueId] = deferred

        return deferred
    }

    open fun handle(payload: P): R? {
        return null
    }

    fun isSource(uniqueId: UUID): Boolean {
        return HANDLER_LIST.flatMap { publisher -> publisher.payloads.keys }.contains(uniqueId)
    }

    open fun register() {
        register(this)
    }

    open fun unregister() {
        unregister(this)
    }

    fun registered(): Boolean {
        return HANDLER_LIST.contains(this)
    }

    companion object {
        val HANDLER_LIST = mutableListOf<Publisher<out ConnectionMeta, *, *>>()

        fun <M : ConnectionMeta, P : Any, R : Any> register(publisher: Publisher<M, P, R>) {
            require(!HANDLER_LIST.contains(publisher)) { "Publisher with id ${publisher.messageMeta.id()} is already registered" }
            HANDLER_LIST.add(publisher)
        }

        fun <M : ConnectionMeta, P : Any, R : Any> unregister(publisher: Publisher<M, P, R>) {
            HANDLER_LIST.remove(publisher)
        }
    }

}