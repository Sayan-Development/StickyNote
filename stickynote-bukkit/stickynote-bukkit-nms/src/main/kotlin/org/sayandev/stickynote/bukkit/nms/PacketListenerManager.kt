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
import org.sayandev.stickynote.bukkit.nms.event.ContainerItemEvent
import org.sayandev.stickynote.bukkit.nms.event.PacketEvent
import org.sayandev.stickynote.bukkit.nms.event.PlayerActionEvent
import org.sayandev.stickynote.bukkit.onlinePlayers
import org.sayandev.stickynote.bukkit.plugin
import org.sayandev.stickynote.bukkit.unregisterListener
import org.sayandev.stickynote.bukkit.nms.accessors.ClientboundContainerSetContentPacketAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.ClientboundContainerSetSlotPacketAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.ServerboundInteractPacketAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.ServerboundPlayerActionPacketAccessor
import org.sayandev.stickynote.bukkit.nms.event.PlayerInteractAtEntityEvent

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

    fun injectPlayer(player: Player) {
        val channelDuplexHandler = object : ChannelDuplexHandler() {
            override fun channelRead(context: ChannelHandlerContext, packet: Any) {
                var packet = packet
                try {
                    val packetContainer = PacketContainer(packet)
                    var isCancelled = false

                    for (packetEvent in packetEvents) {
                        try {
                            val result = packetEvent.onServerboundPacket(player, packetContainer)
                            isCancelled = result.isCancelled
                            if (!isCancelled) {
                                packet = result.packetContainer.packet
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            error(
                                "An error occured while handling (reading) a packet. Please report this error to the plugin's author(s): " +
                                        plugin.description.authors
                            )
                        }
                    }

                    if (!isCancelled) {
                        if (packet.javaClass == ServerboundPlayerActionPacketAccessor.TYPE && PlayerActionEvent.HANDLER_LIST.isNotEmpty()) {
                            PlayerActionEvent.HANDLER_LIST.forEach { event -> event.handle(player, packet) }
                        } else if (packet.javaClass == ServerboundInteractPacketAccessor.TYPE && PlayerInteractAtEntityEvent.HANDLER_LIST.isNotEmpty()) {
                            PlayerInteractAtEntityEvent.HANDLER_LIST.forEach { event -> event.handle(player, packet) }
                        }

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
                var packet = packet
                try {
                    val packetContainer = PacketContainer(packet)
                    var isCancelled = false

                    for (packetEvent in packetEvents) {
                        try {
                            val result = packetEvent.onClientboundPacket(player, packetContainer)
                            isCancelled = result.isCancelled
                            if (!isCancelled) {
                                packet = result.packetContainer.packet
                            }
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
                plugin.description.name.lowercase(),
                channelDuplexHandler
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removePlayer(player: Player) {
        try {
            val channel = NMSUtils.getChannel(player)
            val identifier = plugin.description.name.lowercase()
            channel.eventLoop().execute {
                if (channel.pipeline().get(identifier) != null) {
                    channel.pipeline().remove(identifier)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shutdown() {
        unregisterListener(this)
        onlinePlayers.forEach { player: Player ->
            removePlayer(player)
        }
    }
}