package org.sayandev.stickynote.velocity.messaging

import com.google.gson.Gson
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.subscriber.Subscriber
import java.nio.charset.StandardCharsets

abstract class PluginMessageSubscriber<P, S>(
    namespace: String,
    channel: String,
    payloadClass: Class<P>
): Subscriber<P, S>(
    channel,
    payloadClass
) {

    val channelIdentifier = MinecraftChannelIdentifier.create(namespace, channel)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Subscribe
    fun onMessageReceived(event: PluginMessageEvent) {
        if (event.source !is ServerConnection) return
        if (event.identifier != channelIdentifier) return

        val rawMessage = String(event.data, StandardCharsets.UTF_8)
        val payloadWrapper = Gson().fromJson<PayloadWrapper<P>>(rawMessage, payloadClass)
        val result = onSubscribe(payloadWrapper.payload)
        result.invokeOnCompletion {
            (event.source as ServerConnection).sendPluginMessage(channelIdentifier, Gson().toJson(PayloadWrapper(payloadWrapper.uniqueId, result.getCompleted())).toByteArray())
        }
    }

}