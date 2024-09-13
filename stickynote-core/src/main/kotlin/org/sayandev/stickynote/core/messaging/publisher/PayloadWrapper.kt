package org.sayandev.stickynote.core.messaging.publisher

import java.util.UUID

data class PayloadWrapper<P>(
    val uniqueId: UUID = UUID.randomUUID(),
    val payload: P,
    val state: State = State.FORWARD,
    var source: String? = null
) {
    constructor(payload: P, state: State = State.FORWARD): this(UUID.randomUUID(), payload, state)

    enum class State {
        PROXY,
        FORWARD,
        RESPOND
    }
}