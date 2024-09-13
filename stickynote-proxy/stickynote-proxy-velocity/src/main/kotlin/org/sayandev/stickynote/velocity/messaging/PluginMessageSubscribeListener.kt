package org.sayandev.stickynote.velocity.messaging

import com.google.gson.Gson
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.subscriber.Subscriber
import org.sayandev.stickynote.velocity.*
import java.nio.charset.StandardCharsets

class PluginMessageSubscribeListener<P, S>(
    namespace: String,
    val channel: String,
    val payloadClass: Class<P>
) {

    val channelIdentifier = MinecraftChannelIdentifier.create(namespace, channel)

    init {
        server.channelRegistrar.register(channelIdentifier)
        registerListener(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Subscribe
    fun onMessageReceived(event: PluginMessageEvent) {
        val source = event.source as? ServerConnection ?: return
        if (event.identifier != channelIdentifier) return

        val rawMessage = String(event.data, StandardCharsets.UTF_8)
        val payloadWrapper = Gson().fromJson<PayloadWrapper<P>>(rawMessage, PayloadWrapper::class.java)
        when (payloadWrapper.state) {
            PayloadWrapper.State.PROXY -> {
                launch {
                    val result = (Subscriber.HANDLER_LIST.find { it.channel == channel } as Subscriber<P, S>).onSubscribe(Gson().fromJson(Gson().toJson(payloadWrapper.payload), payloadClass))
                    result.invokeOnCompletion {
                        (event.source as ServerConnection).sendPluginMessage(channelIdentifier, Gson().toJson(PayloadWrapper(payloadWrapper.uniqueId, result.getCompleted(), PayloadWrapper.State.RESPOND, payloadWrapper.source), PayloadWrapper::class.java).toByteArray())
                    }
                }
            }
            PayloadWrapper.State.FORWARD -> {
                for (server in StickyNote.server.allServers) {
                    server.sendPluginMessage(channelIdentifier, Gson().toJson(payloadWrapper.apply {
                        this.source = source.serverInfo.name
                    }, PayloadWrapper::class.java).toByteArray())
                }
            }
            PayloadWrapper.State.RESPOND -> {
                val payloadSource = payloadWrapper.source ?: throw IllegalArgumentException("Can't respond a message if the source is null (payload: ${payloadWrapper})")
                val sourceServer = StickyNote.server.allServers.find { it.serverInfo.name.lowercase() == payloadSource.lowercase() } ?: throw IllegalArgumentException("Can't find the source server on proxy (payload: ${payloadWrapper})")
                sourceServer.sendPluginMessage(channelIdentifier, Gson().toJson(PayloadWrapper(payloadWrapper.uniqueId, payloadWrapper.payload, payloadWrapper.state, payloadSource), PayloadWrapper::class.java).toByteArray())
            }
        }
    }

}