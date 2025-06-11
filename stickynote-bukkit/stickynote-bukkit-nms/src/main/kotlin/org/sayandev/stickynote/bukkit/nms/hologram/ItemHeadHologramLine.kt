package org.sayandev.stickynote.bukkit.nms.hologram

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.nms.enum.EquipmentSlot
import org.sayandev.stickynote.bukkit.nms.npc.entity.ArmorStandNPC

/**
 * ItemHeadHologramLine displays a static item in an ArmorStand head using an ItemEntity (Like a dropped item)
 * @see Hologram
 */
open class ItemHeadHologramLine(
    private var item: ItemStack,
    val glowing: Boolean,
    private var yaw: Float,
    private var pitch: Float,
    distance: Float
): ItemHoloLine(distance) {
    constructor(
        item: ItemStack,
        glowing: Boolean,
        distance: Float
    ): this(item, glowing, 0f, 0f, distance)

    /**
     * Gets the item of the hologram line
     * @return ItemStack of the hologram line
     */
    override fun getItem(): ItemStack {
        return item
    }

    /**
     * Sets the item of the hologram line
     * @param item ItemStack to set
     */
    override fun setItem(item: ItemStack) {
        this.item = item
        getArmorStandNPC().setEquipment(EquipmentSlot.HEAD, item)
    }

    /**
     * Sets the item of the hologram line for a player only. The hologram line will not be updated for other players
     * @param item ItemStack to set
     * @param player Player to set the item for
     */
    override fun setItem(item: ItemStack, player: Player) {
        this.item = item
        getArmorStandNPC().setEquipment(EquipmentSlot.HEAD, item, setOf(player))
    }

    fun look(yaw: Float, pitch: Float) {
        this.yaw = yaw
        this.pitch = pitch
        getArmorStandNPC().look(yaw, pitch)
    }

    fun look(yaw: Float) {
        look(yaw, 0f)
    }

    fun yaw(): Float {
        return yaw
    }

    fun pitch(): Float {
        return pitch
    }

    /**
     * Checks if the item is glowing
     * @return true if the item is glowing
     */
    override fun isGlowing(): Boolean {
        return glowing
    }

    /**
     * Sets the item to glow
     * @param glowing true to make the item glow
     */
    override fun setGlowing(glowing: Boolean) {
        npc.setGlowing(glowing)
    }

    override fun initializeNPC(location: Location) {
        npc = ArmorStandNPC(location).apply {
            this.setEquipment(EquipmentSlot.HEAD, item)
            this.setNoGravity(true)
            this.setMarker(true)
            this.setInvisible(true)
        }
    }

    internal fun getArmorStandNPC(): ArmorStandNPC {
        return npc as ArmorStandNPC
    }

}