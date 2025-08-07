package org.sayandev.stickynote.velocity.messaging

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.ServerConnection
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.publisher.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.subscriber.Subscriber
import org.sayandev.stickynote.velocity.*
import java.nio.charset.StandardCharsets

class PluginMessageSubscribeListener<P, S>(
    namespace: String,
    name: String,
    val payloadClass: Class<P>
) {

    val channelIdentifier = MinecraftChannelIdentifier.create(namespace, name)

    init {
        server.channelRegistrar.register(channelIdentifier)
        registerListener(this)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Subscribe
    fun onMessageReceived(event: PluginMessageEvent) {
        if (event.source !is ServerConnection) return
        val source = event.source as? ServerConnection ?: return
        if (event.identifier != channelIdentifier) return

        val rawMessage = String(event.data, StandardCharsets.UTF_8)
        val payloadWrapper = rawMessage.asPayloadWrapper<P>()

        when (payloadWrapper.state) {
            PayloadWrapper.State.PROXY -> {
                launch {
                    val result = (Subscriber.HANDLER_LIST.find { it.namespace == channelIdentifier.namespace && it.name == channelIdentifier.name } as Subscriber<P, S>).onSubscribe(payloadWrapper.typedPayload(payloadClass))
                    result?.invokeOnCompletion {
                        (event.source as ServerConnection).sendPluginMessage(channelIdentifier, PayloadWrapper(payloadWrapper.uniqueId, result.getCompleted(), PayloadWrapper.State.RESPOND, payloadWrapper.source).asJson().toByteArray())
                    }
                }
            }
            PayloadWrapper.State.FORWARD -> {
                launch {
                    val targetServerName = payloadWrapper.target
                    val targetServer = StickyNote.server.allServers.find { it.serverInfo.name == targetServerName }
                    val result = (Subscriber.HANDLER_LIST.find { it.namespace == channelIdentifier.namespace && it.name == channelIdentifier.name } as? Subscriber<P, S>)?.onSubscribe(payloadWrapper.typedPayload(payloadClass))
                    result?.invokeOnCompletion {
                        if (targetServerName != null) {
                            targetServer?.sendPluginMessage(channelIdentifier, PayloadWrapper(payloadWrapper.uniqueId, result.getCompleted(), payloadWrapper.state, source.serverInfo.name, targetServerName).asJson().toByteArray()) ?: warn("target server name was specified as ${targetServerName} but there's not server with this id. will ignore pluginmessage request")
                        } else {
                            for (server in StickyNote.server.allServers) {
                                if (payloadWrapper.excludeSource && source.serverInfo.name == server.serverInfo.name) continue
                                server.sendPluginMessage(channelIdentifier, PayloadWrapper(payloadWrapper.uniqueId, result.getCompleted(), payloadWrapper.state, source.serverInfo.name, server.serverInfo.name).asJson().toByteArray())
                            }
                        }
                    } ?: let {
                        if (targetServerName != null) {
                            targetServer?.sendPluginMessage(channelIdentifier, PayloadWrapper(payloadWrapper.uniqueId, payloadWrapper.payload, payloadWrapper.state, source.serverInfo.name, targetServerName).asJson().toByteArray()) ?: warn("target server name was specified as ${targetServerName} but there's not server with this id. will ignore pluginmessage request")
                        } else {
                            for (server in StickyNote.server.allServers) {
                                if (payloadWrapper.excludeSource && source.serverInfo.name == server.serverInfo.name) continue
                                server.sendPluginMessage(channelIdentifier, PayloadWrapper(payloadWrapper.uniqueId, payloadWrapper.payload, payloadWrapper.state, source.serverInfo.name, server.serverInfo.name).asJson().toByteArray())
                            }
                        }
                    }
                }
            }
            PayloadWrapper.State.RESPOND -> {
                val payloadSource = payloadWrapper.source ?: throw IllegalArgumentException("Can't respond a message if the source is null (payload: ${payloadWrapper})")
                val sourceServer = StickyNote.server.allServers.find { it.serverInfo.name.lowercase() == payloadSource.lowercase() } ?: throw IllegalArgumentException("Can't find the source server on proxy (payload: ${payloadWrapper})")
                sourceServer.sendPluginMessage(channelIdentifier, PayloadWrapper(payloadWrapper.uniqueId, payloadWrapper.payload, payloadWrapper.state, payloadSource, payloadWrapper.target).asJson().toByteArray())
            }
        }
    }

}