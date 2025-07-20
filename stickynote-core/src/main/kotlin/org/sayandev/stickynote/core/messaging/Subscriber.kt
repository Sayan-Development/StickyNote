package org.sayandev.stickynote.core.messaging

import kotlinx.coroutines.Deferred

abstract class Subscriber<P : Any, R : Any>(
    val messageMeta: MessageMeta<P, R>,
) {

    abstract suspend fun onSubscribe(payload: P): Deferred<R>

    fun register() {
        register(this)
    }

    fun unregister() {
        unregister(this)
    }

    companion object {
        val HANDLER_LIST = mutableListOf<Subscriber<*, *>>()

        val isVelocity = runCatching { Class.forName("com.velocitypowered.api.proxy.ProxyServer") }.isSuccess

        fun <P : Any, R : Any> register(subscriber: Subscriber<P, R>) {
            HANDLER_LIST.add(subscriber)
        }

        fun <P : Any, R : Any> unregister(subscriber: Subscriber<P, R>) {
            HANDLER_LIST.remove(subscriber)
        }
    }

}