package org.sayandev.stickynote.core.messaging.publisher

import kotlinx.coroutines.CompletableDeferred

abstract class Publisher<P, S>(
    val channel: String
) {

    val payloads: MutableMap<P, CompletableDeferred<S>> = mutableMapOf()

    fun publish(payload: P): CompletableDeferred<S> {
        val deferred = CompletableDeferred<S>()

        payloads[payload] = deferred

        //onPublish(payload)

        return deferred
    }

    //abstract fun onPublish(payload: P): S

    companion object {
        val HANDLER_LIST = mutableListOf<Publisher<*, *>>()

        fun <P, S> register(publisher: Publisher<P, S>) {
            HANDLER_LIST.add(publisher)
        }

        fun <P, S> unregister(publisher: Publisher<P, S>) {
            HANDLER_LIST.remove(publisher)
        }
    }

}