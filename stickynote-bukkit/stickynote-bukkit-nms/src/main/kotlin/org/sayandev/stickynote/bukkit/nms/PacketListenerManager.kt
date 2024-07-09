package org.sayandev.stickynote.bukkit.nms

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPipeline
import io.netty.channel.ChannelPromise
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.sayandev.stickynote.bukkit.StickyNote
import org.sayandev.stickynote.bukkit.nms.event.ContainerItemEvent
import org.sayandev.stickynote.bukkit.nms.event.PacketEvent
import org.sayandev.stickynote.bukkit.nms.event.PlayerActionEvent
import org.sayandev.stickynote.bukkit.onlinePlayers
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.unregisterListener
import org.sayandev.stickynote.nms.accessors.ClientboundContainerSetContentPacketAccessor
import org.sayandev.stickynote.nms.accessors.ClientboundContainerSetSlotPacketAccessor
import org.sayandev.stickynote.nms.accessors.ServerboundInteractPacketAccessor
import org.sayandev.stickynote.nms.accessors.ServerboundPlayerActionPacketAccessor
import java.nio.channels.Channel
import java.util.concurrent.Callable

object PacketListenerManager: Listener {

    private val packetEvents: MutableSet<PacketEvent> = HashSet()

    fun register(packetEvent: PacketEvent) {
        packetEvents.add(packetEvent)
    }

    fun unregister(packetEvent: PacketEvent) {
        packetEvents.remove(packetEvent)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(event: PlayerJoinEvent) {
        injectPlayer(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onQuit(event: PlayerQuitEvent) {
        removePlayer(event.player)
    }

    private fun injectPlayer(player: Player) {
        val channelDuplexHandler: ChannelDuplexHandler = object : ChannelDuplexHandler() {
            override fun channelRead(context: ChannelHandlerContext, packet: Any) {
                try {
                    val packetContainer: PacketContainer = PacketContainer(packet)
                    var isCancelled = false

                    for (packetEvent in packetEvents) {
                        try {
                            isCancelled = !packetEvent.onServerboundPacket(player, packetContainer)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            error(
                                "An error occured while handling (reading) a packet. Please report this error to the plugin's author(s): " +
                                        plugin.description.authors
                            )
                        }
                    }

                    if (!isCancelled) {
                        if (packet.javaClass == ServerboundPlayerActionPacketAccessor.TYPE && !PlayerActionEvent.HANDLER_LIST.isEmpty()) {
                            PlayerActionEvent.HANDLER_LIST.forEach { event -> event.handle(player, packet) }
                        } /*TODO PlayerInteractAtEntityEvent
                            else if (packet.javaClass == ServerboundInteractPacketAccessor.getType() && !PlayerInteractAtEntityEvent.HANDLER_LIST.isEmpty()) {
                            PlayerInteractAtEntityEvent.HANDLER_LIST.forEach { event -> event.handle(player, packet) }
                        }*/

                        try {
                            super.channelRead(context, packet)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (ignored: IllegalArgumentException) {
                }
            }

            override fun write(context: ChannelHandlerContext, packet: Any, channelPromise: ChannelPromise) {
                try {
                    val packetContainer: PacketContainer = PacketContainer(packet)
                    var isCancelled = false

                    for (packetEvent in packetEvents) {
                        try {
                            isCancelled = !packetEvent.onClientboundPacket(player, packetContainer)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            error(
                                "An error occured while handling (writing) a packet. Please report this error to the plugin's author(s): " +
                                        plugin.description.authors
                            )
                        }
                    }

                    if ((packet.javaClass == ClientboundContainerSetSlotPacketAccessor.TYPE || packet.javaClass == ClientboundContainerSetContentPacketAccessor.TYPE) &&ContainerItemEvent.HANDLER_LIST.isNotEmpty()) {
                        ContainerItemEvent.HANDLER_LIST.forEach { event -> event.handle(player, packet) }
                    }

                    if (!isCancelled) {
                        try {
                            super.write(context, packet, channelPromise)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (ignored: IllegalArgumentException) {
                }
            }
        }

        try {
            val pipeline: ChannelPipeline = NMSUtils.getChannel(player).pipeline()
            pipeline.addBefore(
                "packet_handler",
                String.format("%s_%s", plugin.description.name, player.name),
                channelDuplexHandler
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removePlayer(player: Player) {
        try {
            val channel = NMSUtils.getChannel(player)
            channel.eventLoop().submit<Any>(Callable<Any?> {
                channel.pipeline().remove(
                    String.format(
                        "%s_%s",
                        plugin.description.name,
                        player.name
                    )
                )
                null
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shutdown() {
        unregisterListener(this)
        onlinePlayers.forEach { player: Player ->
            removePlayer(
                player
            )
        }
    }
}