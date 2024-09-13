package org.sayandev.stickynote.core.messaging.subscriber

import kotlinx.coroutines.CompletableDeferred

abstract class Subscriber<P, S>(
    val channel: String,
    val payloadClass: Class<P>
) {

    abstract fun onSubscribe(payload: P): CompletableDeferred<S>

    companion object {

    }

}