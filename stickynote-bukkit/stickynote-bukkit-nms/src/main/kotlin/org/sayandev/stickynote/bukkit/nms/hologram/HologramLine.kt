package org.sayandev.stickynote.bukkit.nms.hologram

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.extension.toNmsComponent
import org.sayandev.stickynote.bukkit.nms.NMSUtils.sendPacket
import org.sayandev.stickynote.bukkit.nms.PacketUtils
import org.sayandev.stickynote.bukkit.nms.npc.entity.ArmorStandNPC
import org.sayandev.stickynote.bukkit.nms.accessors.EntityAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.EntityDataAccessorAccessor

/**
 * HologramLine displays a Component in a Hologram
 * @see Hologram
 */
class HologramLine(
    component: Component,
    distance: Float
): HoloLine(distance) {

    var component: Component = component
        set(value) {
            field = value
            npc.setCustomName(value)
        }

    /**
     * Sets the component of the hologram line for a player only. The hologram line will not be updated for other players
     * @param component Component to set
     */
    fun setComponent(component: Component, player: Player) {
        player.sendPacket(PacketUtils.getEntityDataPacket(
            npc.entityId,
            EntityDataAccessorAccessor.METHOD_GET_ID!!.invoke(EntityAccessor.FIELD_DATA_CUSTOM_NAME!!) as Int,
            component.toNmsComponent()
        ))
    }

    override fun initializeNPC(location: Location) {
        npc = ArmorStandNPC(location)
        npc.setInvisible(true)
        npc.setCustomNameVisible(true)
        npc.setCustomName(component)
        getArmorStandNPC().setNoBasePlate(true)
        getArmorStandNPC().setSmall(true)
        getArmorStandNPC().setMarker(true)
    }

    internal fun getArmorStandNPC(): ArmorStandNPC {
        return npc as ArmorStandNPC
    }
}