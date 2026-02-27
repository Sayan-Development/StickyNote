package org.sayandev.stickynote.paper.nms.event

import org.bukkit.entity.Player
import org.sayandev.stickynote.core.math.Vector3
import org.sayandev.stickynote.paper.nms.accessors.DirectionAccessor
import org.sayandev.stickynote.paper.nms.accessors.ServerboundPlayerActionPacketAccessor
import org.sayandev.stickynote.paper.nms.accessors.ServerboundPlayerActionPacket_ActionAccessor
import org.sayandev.stickynote.paper.nms.accessors.Vec3iAccessor
import java.util.*

abstract class PlayerActionEvent : PacketListener {

    init {
        register()
    }

    protected abstract fun onStartDig(player: Player, blockPos: Vector3, direction: Direction)

    protected abstract fun onStopDig(player: Player, blockPos: Vector3)

    protected abstract fun onDropAllItems(player: Player)

    protected abstract fun onDropItem(player: Player)

    protected abstract fun onUseItemRelease(player: Player)

    protected abstract fun onSwapItemsWithOffHand(player: Player)

    final override fun register() {
        HANDLER_LIST.add(this)
    }

    override fun unregister() {
        HANDLER_LIST.remove(this)
    }

    enum class Direction(private val nmsObject: Any) {
        DOWN(DirectionAccessor.FIELD_DOWN!!),
        UP(DirectionAccessor.FIELD_UP!!),
        NORTH(DirectionAccessor.FIELD_NORTH!!),
        SOUTH(DirectionAccessor.FIELD_SOUTH!!),
        WEST(DirectionAccessor.FIELD_WEST!!),
        EAST(DirectionAccessor.FIELD_EAST!!)
    }

    override fun handle(player: Player, packet: Any) {
        try {
            val action: Any = ServerboundPlayerActionPacketAccessor.METHOD_GET_ACTION!!.invoke(packet)
            val nmsBlockPos: Any = ServerboundPlayerActionPacketAccessor.METHOD_GET_POS!!.invoke(packet)
            val nmsDirection: Any = ServerboundPlayerActionPacketAccessor.METHOD_GET_DIRECTION!!.invoke(packet)

            val direction: Direction = Direction.valueOf(
                (DirectionAccessor.METHOD_GET_NAME!!.invoke(nmsDirection) as String).uppercase()
            )
            val blockPos: Vector3 = Vector3.at(
                Vec3iAccessor.METHOD_GET_X!!.invoke(nmsBlockPos) as Int,
                Vec3iAccessor.METHOD_GET_Y!!.invoke(nmsBlockPos) as Int,
                Vec3iAccessor.METHOD_GET_Z!!.invoke(nmsBlockPos) as Int
            )

            when (action) {
                ServerboundPlayerActionPacket_ActionAccessor.FIELD_START_DESTROY_BLOCK -> onStartDig(player, blockPos, direction)
                ServerboundPlayerActionPacket_ActionAccessor.FIELD_STOP_DESTROY_BLOCK,ServerboundPlayerActionPacket_ActionAccessor.FIELD_ABORT_DESTROY_BLOCK -> onStopDig(player, blockPos)
                ServerboundPlayerActionPacket_ActionAccessor.FIELD_DROP_ALL_ITEMS -> onDropAllItems(player)
                ServerboundPlayerActionPacket_ActionAccessor.FIELD_DROP_ITEM -> onDropItem(player)
                ServerboundPlayerActionPacket_ActionAccessor.FIELD_RELEASE_USE_ITEM -> onUseItemRelease(player)
                ServerboundPlayerActionPacket_ActionAccessor.FIELD_SWAP_ITEM_WITH_OFFHAND -> onSwapItemsWithOffHand(player)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val HANDLER_LIST: MutableSet<PlayerActionEvent> = HashSet()
    }
}
