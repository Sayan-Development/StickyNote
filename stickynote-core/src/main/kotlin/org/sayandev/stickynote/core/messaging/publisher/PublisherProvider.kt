package org.sayandev.stickynote.core.messaging.publisher

interface PublisherProvider<P : Any, S : Any> {

    val publishers: List<Publisher<*, P, S>>
}

inline fun <reified T : Publisher<*, *, *>> PublisherProvider<*, *>.publisher(): T {
    return publishers.first { it is T } as T
}