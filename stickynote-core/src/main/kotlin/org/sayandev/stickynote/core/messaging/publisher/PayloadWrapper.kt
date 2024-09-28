package org.sayandev.stickynote.core.messaging.publisher

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import java.util.*
import java.util.logging.Logger
import kotlin.reflect.KClass

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
        private var gson = Gson()
        private val typeAdapters = mutableMapOf<Class<*>, Any>()

        fun registerAdapter(type: Class<*>, adapter: TypeAdapter<*>) {
            typeAdapters[type] = adapter
            updateGson()
        }

        fun registerSerializer(type: Class<*>, serializer: Any) {
            typeAdapters[type] = serializer
            updateGson()
        }

        fun registerDeserializer(type: Class<*>, deserializer: Any) {
            typeAdapters[type] = deserializer
            updateGson()
        }

        fun unregisterAdapter(type: Class<*>) {
            typeAdapters.remove(type)
            updateGson()
        }

        fun unregisterSerializer(type: Class<*>) {
            typeAdapters.remove(type)
            updateGson()
        }

        fun unregisterDeserializer(type: Class<*>) {
            typeAdapters.remove(type)
            updateGson()
        }

        private fun updateGson() {
            val builder = GsonBuilder()
            for ((clazz, adapter) in typeAdapters) {
                builder.registerTypeAdapter(clazz, adapter)
            }
            gson = builder.create()
        }

        fun PayloadWrapper<*>.asJson(): String {
            return gson.toJson(this, PayloadWrapper::class.java)
        }

        fun <P: Any> String.asPayloadWrapper(): PayloadWrapper<P> {
            return gson.fromJson<PayloadWrapper<P>>(this, PayloadWrapper::class.java)
        }

        fun <P: Any> PayloadWrapper<*>.typedPayload(payloadClass: KClass<P>): P {
//            return gson.fromJson(gson.toJson(this.payload), object : TypeToken<P>() {})
            /*Logger.getGlobal().warning("payload wrapper: ${this}")
            Logger.getGlobal().warning("payload: ${this.payload}")
            Logger.getGlobal().warning("payload class: ${payloadClass}")
            Logger.getGlobal().warning("payload class: ${payloadClass as List<*>::class.java}")
            Logger.getGlobal().warning("payload to gson: ${gson.toJson(this.payload, payloadClass)}")
            Logger.getGlobal().warning("payload from gson: ${gson.fromJson(gson.toJson(this.payload, payloadClass), payloadClass)}")*/
            return gson.fromJson(gson.toJson(this.payload, payloadClass.java), payloadClass.java)
        }
    }
}