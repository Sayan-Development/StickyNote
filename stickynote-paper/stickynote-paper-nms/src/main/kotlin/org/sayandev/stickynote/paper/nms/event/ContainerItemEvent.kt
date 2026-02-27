package org.sayandev.stickynote.paper.nms.event

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.paper.nms.NMSUtils
import org.sayandev.stickynote.paper.nms.accessors.ClientboundContainerSetContentPacketAccessor
import org.sayandev.stickynote.paper.nms.accessors.ClientboundContainerSetSlotPacketAccessor

abstract class ContainerItemEvent : PacketListener {

    init {
        register()
    }

    abstract fun onItemUpdate(player: Player, item: ItemStack): ItemStack

    final override fun register() {
        HANDLER_LIST.add(this)
    }

    override fun unregister() {
        HANDLER_LIST.remove(this)
    }

    override fun handle(player: Player, packet: Any) {
        try {
            if (packet.javaClass == ClientboundContainerSetSlotPacketAccessor.TYPE) {
                val item: ItemStack = NMSUtils.getBukkitItemStack(
                    ClientboundContainerSetSlotPacketAccessor.FIELD_ITEM_STACK!!.get(packet)
                )
                if (item.type.isAir) return
                ClientboundContainerSetSlotPacketAccessor.FIELD_ITEM_STACK!!
                    .set(packet, NMSUtils.getNmsItemStack(onItemUpdate(player, item.clone())))
            } else if (packet.javaClass == ClientboundContainerSetContentPacketAccessor.TYPE) {
                val items = (ClientboundContainerSetContentPacketAccessor.FIELD_ITEMS!!.get(packet) as List<*>)
                    .map { NMSUtils.getBukkitItemStack(it!!) }.toMutableList()

                items.replaceAll { item ->
                    if (item.type.isAir) item
                    else onItemUpdate(player, item.clone())
                }

                ClientboundContainerSetContentPacketAccessor.FIELD_ITEMS!!.set(
                    packet,
                    items.map { NMSUtils.getNmsItemStack(it) }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        var HANDLER_LIST: MutableSet<ContainerItemEvent> = HashSet()
    }
}