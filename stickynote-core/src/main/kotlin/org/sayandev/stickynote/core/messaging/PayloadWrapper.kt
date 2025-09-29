package org.sayandev.stickynote.core.messaging

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonIOException
import com.google.gson.TypeAdapter
import java.util.*
import kotlin.reflect.KClass

data class PayloadWrapper<P>(
    val uniqueId: UUID = UUID.randomUUID(),
    val payload: P,
    val behaviour: PayloadBehaviour = PayloadBehaviour.FORWARD,
    var source: String? = null,
    val target: String? = null,
    val excludeSource: Boolean = false,
) {
    constructor(payload: P, behaviour: PayloadBehaviour = PayloadBehaviour.FORWARD): this(UUID.randomUUID(), payload, behaviour)

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

        fun <P> String.asPayloadWrapper(): PayloadWrapper<P> {
            return gson.fromJson<PayloadWrapper<P>>(this, PayloadWrapper::class.java)
        }

        fun <P> String.asPayloadWrapper(clazz: Class<P>): PayloadWrapper<P> {
            return gson.fromJson<PayloadWrapper<P>>(this, PayloadWrapper::class.java)
        }

        fun <P> String.asOptionalPayloadWrapper(): PayloadWrapper<P>? {
            return try {
                gson.fromJson<PayloadWrapper<P>>(this, PayloadWrapper::class.java)
            } catch (_: Exception) { null }
        }

        fun <P : Any> PayloadWrapper<*>.typedPayload(payloadType: KClass<P>): P {
            return try {
                gson.fromJson(gson.toJson(this.payload), payloadType.java)
            } catch (e: JsonIOException) {
                throw IllegalStateException("Could not convert payload to $this", e)
            }
        }

        fun <P> P.toPayloadWrapper(behaviour: PayloadBehaviour = PayloadBehaviour.FORWARD): PayloadWrapper<P> {
            return PayloadWrapper(
                payload = this,
                behaviour = behaviour
            )
        }
    }
}