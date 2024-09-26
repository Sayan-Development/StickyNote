package org.sayandev.stickynote.core.messaging.publisher

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import java.util.*

data class PayloadWrapper<P>(
    val uniqueId: UUID = UUID.randomUUID(),
    val payload: P,
    val state: State = State.FORWARD,
    var source: String? = null,
    val target: String? = null,
    val excludeSource: Boolean = false,
) {
    constructor(payload: P, state: State = State.FORWARD): this(UUID.randomUUID(), payload, state)

    enum class State {
        PROXY,
        FORWARD,
        RESPOND
    }

    companion object {
        var gson = Gson()
        val typeAdapters = mutableMapOf<Class<*>, TypeAdapter<*>>()

        fun registerAdapter(type: Class<*>, adapter: TypeAdapter<*>) {
            typeAdapters[type] = adapter
        }

        fun unregisterAdapter(type: Class<*>) {
            typeAdapters.remove(type)
        }

        fun updateGson() {
            val builder = GsonBuilder()
            for ((clazz, adapter) in typeAdapters) {
                builder.registerTypeAdapter(clazz, adapter)
            }
            gson = builder.create()
        }

        fun PayloadWrapper<*>.asJson(): String {
            return gson.toJson(this, PayloadWrapper::class.java)
        }

        fun <P> String.asPayloadWrapper(): PayloadWrapper<P> {
            return gson.fromJson<PayloadWrapper<P>>(this, PayloadWrapper::class.java)
        }

        fun <P> PayloadWrapper<*>.typedPayload(payloadClass: Class<P>): P {
            return gson.fromJson(Gson().toJson(this.payload), payloadClass)
        }
    }
}