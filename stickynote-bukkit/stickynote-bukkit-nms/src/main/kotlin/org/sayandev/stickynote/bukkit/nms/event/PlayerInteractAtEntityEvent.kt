package org.sayandev.stickynote.bukkit.nms.event

import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.nms.enum.InteractionHand
import org.sayandev.stickynote.bukkit.utils.ServerVersion
import org.sayandev.stickynote.core.math.Vector3
import org.sayandev.stickynote.bukkit.nms.accessors.*

/**
 * A packet-based event that triggers whenever a player interact on both server-sided and client-sided (sent by packets) entities.
 * Usable for NPCs.
 */
abstract class PlayerInteractAtEntityEvent : PacketListener {

    init {
        register()
    }

    protected open fun onInteract(player: Player?, hand: InteractionHand?, entityId: Int) { }

    protected open fun onInteractAt(player: Player?, hand: InteractionHand?, location: Vector3?, entityId: Int) { }

    protected open fun onAttack(player: Player?, entityId: Int) { }

    final override fun register() {
        HANDLER_LIST.add(this)
    }

    override fun unregister() {
        HANDLER_LIST.remove(this)
    }

    override fun handle(player: Player, packet: Any) {
        try {
            val entityId = ServerboundInteractPacketAccessor.FIELD_ENTITY_ID!!.get(packet) as Int
            val action: Any = ServerboundInteractPacketAccessor.FIELD_ACTION!!.get(packet)
            var actionId = -1
            var hand: InteractionHand? = null
            var location: Vector3? = null

            if (ServerVersion.supports(17)) {
                val actionType: Any = ServerboundInteractPacket_ActionAccessor.METHOD_GET_TYPE!!.apply { this.isAccessible = true }.invoke(action)
                when (actionType) {
                    ServerboundInteractPacket_ActionTypeAccessor.FIELD_ATTACK!! -> {
                        actionId = 0
                    }
                    ServerboundInteractPacket_ActionTypeAccessor.FIELD_INTERACT!! -> {
                        actionId = 1
                        hand = InteractionHand.fromNmsObject(ServerboundInteractPacket_InteractionActionAccessor.FIELD_HAND!!.get(action))
                    }
                    ServerboundInteractPacket_ActionTypeAccessor.FIELD_INTERACT_AT!! -> {
                        actionId = 2
                        hand = InteractionHand.fromNmsObject(ServerboundInteractPacket_InteractionAtLocationActionAccessor.FIELD_HAND!!.get(action))
                        val vec3: Any = ServerboundInteractPacket_InteractionAtLocationActionAccessor.FIELD_LOCATION!!.get(action)
                        location = Vector3.at(
                            Vec3Accessor.METHOD_X!!.invoke(vec3) as Double,
                            Vec3Accessor.METHOD_Y!!.invoke(vec3) as Double,
                            Vec3Accessor.METHOD_Z!!.invoke(vec3) as Double
                        )
                    }
                }
            } else {
                when (action) {
                    ServerboundInteractPacket_ActionAccessor.FIELD_ATTACK!! -> {
                        actionId = 0
                    }
                    ServerboundInteractPacket_ActionAccessor.FIELD_INTERACT!! -> {
                        actionId = 1
                        if (ServerVersion.supports(9)) {
                            hand = InteractionHand.fromNmsObject(ServerboundInteractPacketAccessor.FIELD_HAND!!.get(packet))
                        }
                    }
                    ServerboundInteractPacket_ActionAccessor.FIELD_INTERACT_AT!! -> {
                        actionId = 2
                        val vec3: Any = ServerboundInteractPacketAccessor.FIELD_LOCATION!!.get(packet)
                        if (ServerVersion.supports(9)) {
                            hand = InteractionHand.fromNmsObject(ServerboundInteractPacketAccessor.FIELD_HAND!!.get(packet))
                        }
                        location = if (ServerVersion.supports(14)) {
                            Vector3.at(
                                Vec3Accessor.METHOD_X!!.invoke(vec3) as Double,
                                Vec3Accessor.METHOD_Y!!.invoke(vec3) as Double,
                                Vec3Accessor.METHOD_Z!!.invoke(vec3) as Double
                            )
                        } else {
                            Vector3.at(
                                Vec3Accessor.FIELD_X!!.get(vec3) as Double,
                                Vec3Accessor.FIELD_Y!!.get(vec3) as Double,
                                Vec3Accessor.FIELD_Z!!.get(vec3) as Double
                            )
                        }
                    }
                }
            }

            when (actionId) {
                0 -> {
                    onAttack(player, entityId)
                }

                1 -> {
                    onInteract(player, hand, entityId)
                }

                2 -> {
                    onInteractAt(player, hand, location, entityId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val HANDLER_LIST: MutableSet<PlayerInteractAtEntityEvent> = HashSet()
    }
}
