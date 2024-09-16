package org.sayandev.stickynote.core.messaging.publisher

import com.google.gson.Gson
import java.util.UUID

data class PayloadWrapper<P>(
    val uniqueId: UUID = UUID.randomUUID(),
    val payload: P,
    val state: State = State.FORWARD,
    var source: String? = null,
    val target: String? = null,
    val excludeSource: Boolean = false
) {
    constructor(payload: P, state: State = State.FORWARD): this(UUID.randomUUID(), payload, state)

    enum class State {
        PROXY,
        FORWARD,
        RESPOND
    }

    companion object {
        fun PayloadWrapper<*>.asJson(): String {
            return Gson().toJson(this, PayloadWrapper::class.java)
        }

        fun <P> String.asPayloadWrapper(): PayloadWrapper<P> {
            return Gson().fromJson<PayloadWrapper<P>>(this, PayloadWrapper::class.java)
        }

        fun <P> PayloadWrapper<*>.typedPayload(payloadClass: Class<P>): P {
            return Gson().fromJson(Gson().toJson(this.payload), payloadClass)
        }
    }
}