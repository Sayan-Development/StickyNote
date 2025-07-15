package org.sayandev.stickynote.velocity.messaging

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import com.velocitypowered.api.proxy.server.RegisteredServer
import kotlinx.coroutines.CompletableDeferred
import org.sayandev.stickynote.core.messaging.MessageMeta
import org.sayandev.stickynote.core.messaging.PayloadBehaviour
import org.sayandev.stickynote.core.messaging.PayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asJson
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.asPayloadWrapper
import org.sayandev.stickynote.core.messaging.PayloadWrapper.Companion.typedPayload
import org.sayandev.stickynote.core.messaging.SimpleConnectionMeta
import org.sayandev.stickynote.core.messaging.Publisher
import org.sayandev.stickynote.velocity.registerListener
import org.sayandev.stickynote.velocity.server
import java.util.logging.Logger

abstract class PluginMessagePublisher<P : Any, R : Any>(
    messageMeta: MessageMeta<P, R>,
    connectionMeta: SimpleConnectionMeta,
    logger: Logger
): Publisher<SimpleConnectionMeta, P, R>(
    messageMeta,
    connectionMeta,
    logger
) {

    val channelIdentifier: MinecraftChannelIdentifier = MinecraftChannelIdentifier.create(messageMeta.namespace, messageMeta.name)

    init {
        registerChannel()
    }

    private fun registerChannel() {
        server.channelRegistrar.register(channelIdentifier)
        registerListener(this)
    }

    suspend fun publish(server: RegisteredServer, payloadWrapper: PayloadWrapper<P>): CompletableDeferred<R> {
        server.sendPluginMessage(channelIdentifier, payloadWrapper.asJson().toByteArray())
        return publish(payloadWrapper)
    }

    suspend fun publish(player: Player, payloadWrapper: PayloadWrapper<P>): CompletableDeferred<R> {
        player.sendPluginMessage(channelIdentifier, payloadWrapper.asJson().toByteArray())
        return publish(payloadWrapper)
    }

    abstract override fun handle(payload: P): R?

    @Subscribe
    fun onMessageReceived(event: PluginMessageEvent) {
        val data = event.data
        val channel = event.identifier.id
        if (channel != channelIdentifier.id) return
        val result = String(data, Charsets.UTF_8).asPayloadWrapper<R>()
        when (result.behaviour) {
            PayloadBehaviour.RESPONSE -> {
                for (publisher in HANDLER_LIST.filterIsInstance<PluginMessagePublisher<P, R>>()) {
                    if (messageMeta.id() == channel) {
                        publisher.payloads[result.uniqueId]?.apply {
                            this.complete(result.typedPayload(messageMeta.resultType))
                            publisher.payloads.remove(result.uniqueId)
                        } ?: throw IllegalStateException("No payload found for uniqueId ${result.uniqueId}") // throws exception if the payload doesn't belong to this server
                    }
                }
            }
            PayloadBehaviour.FORWARD_PROXY -> { }
            else -> {
                throw IllegalStateException("a result payload has been received with ${result.behaviour} state, but it doesn't belong here. (payload: ${result})")
            }
        }
    }
}
