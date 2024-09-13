package org.sayandev.stickynote.core.messaging.subscriber

import kotlinx.coroutines.CompletableDeferred

abstract class Subscriber<P, S>(
    val channel: String,
) {

    abstract suspend fun onSubscribe(payload: P): CompletableDeferred<S>

    fun register() {
        register(this)
    }

    fun unregister() {
        unregister(this)
    }

    companion object {
        val HANDLER_LIST = mutableListOf<Subscriber<*, *>>()

        fun <P, S> register(subscriber: Subscriber<P, S>) {
            HANDLER_LIST.add(subscriber)
        }

        fun <P, S> unregister(subscriber: Subscriber<P, S>) {
            HANDLER_LIST.remove(subscriber)
        }
    }

}