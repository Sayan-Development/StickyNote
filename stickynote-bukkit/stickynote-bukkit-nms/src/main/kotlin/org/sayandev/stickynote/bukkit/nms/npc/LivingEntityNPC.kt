package org.sayandev.stickynote.bukkit.nms.npc

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.NMSUtils.sendPacket
import org.sayandev.stickynote.bukkit.nms.PacketUtils
import org.sayandev.stickynote.bukkit.nms.accessors.AttributeInstanceAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.AttributesAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.LivingEntityAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.SynchedEntityDataAccessor
import org.sayandev.stickynote.bukkit.nms.enum.EquipmentSlot
import org.sayandev.stickynote.bukkit.nms.enum.InteractionHand
import org.sayandev.stickynote.bukkit.utils.ServerVersion

abstract class LivingEntityNPC(
    entity: Any,
    location: Location,
    npcType: NPCType
): EntityNPC(entity, location, npcType) {

    private var effectColor = 0
    private var effectAsAmbient = false

    fun setBodyArrows(arrowsOnBody: Int) {
        LivingEntityAccessor.METHOD_SET_ARROW_COUNT!!.invoke(entity, arrowsOnBody)
    }

    fun getBodyArrows(): Int {
        return LivingEntityAccessor.METHOD_GET_ARROW_COUNT!!.invoke(entity) as Int
    }

    fun setEffectColor(effectColor: Int) {
        this.effectColor = effectColor
        SynchedEntityDataAccessor.METHOD_SET!!.invoke(getEntityData(), LivingEntityAccessor.FIELD_DATA_EFFECT_COLOR_ID!!, effectColor)
        sendEntityData()
    }

    fun getEffectColor(): Int {
        return effectColor
    }

    fun setEffectsAsAmbients(asAmbients: Boolean) {
        this.effectAsAmbient = asAmbients
        SynchedEntityDataAccessor.METHOD_SET!!.invoke(
            getEntityData(),
            LivingEntityAccessor.FIELD_DATA_EFFECT_AMBIENCE_ID!!,
            asAmbients
        )
        sendEntityData()
    }

    fun areEffectsAsAmbients(): Boolean {
        return effectAsAmbient
    }

    fun resetEffects() {
        LivingEntityAccessor.METHOD_REMOVE_EFFECT_PARTICLES!!.invoke(entity)
        sendEntityData()
    }

    /**
     * @apiNote > 1.15
     */
    fun setStingerCount(stingerCount: Int) {
        if (!ServerVersion.supports(15)) return
        LivingEntityAccessor.METHOD_SET_STINGER_COUNT!!.invoke(entity)
        sendEntityData()
    }

    /**
     * @apiNote > 1.15
     */
    fun getStingerCount(): Int {
        if (!ServerVersion.supports(15)) return -1
        return LivingEntityAccessor.METHOD_GET_STINGER_COUNT!!.invoke(entity) as Int
    }

    fun startUsingItem() {
        startUsingItem(InteractionHand.MAIN_HAND)
    }

    fun startUsingItem(interactionHand: InteractionHand) {
        LivingEntityAccessor.FIELD_USE_ITEM!!.set(
            entity,
            if (interactionHand == InteractionHand.MAIN_HAND) equipments[EquipmentSlot.MAINHAND] else equipments[EquipmentSlot.OFFHAND]
        )
        if (ServerVersion.supports(13)) {
            LivingEntityAccessor.METHOD_SET_LIVING_ENTITY_FLAG!!.invoke(entity, 1, true)
            LivingEntityAccessor.METHOD_SET_LIVING_ENTITY_FLAG!!.invoke(entity, 2, interactionHand == InteractionHand.OFF_HAND)
        } else {
            var i: Byte = 1
            if (interactionHand == InteractionHand.OFF_HAND) i = (i.toInt() or 2).toByte()
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(getEntityData(), LivingEntityAccessor.FIELD_DATA_LIVING_ENTITY_FLAGS!!, i)
        }
        sendEntityData()
    }

    fun stopUsingItem() {
        LivingEntityAccessor.METHOD_STOP_USING_ITEM!!.invoke(entity)
        sendEntityData()
    }

    fun getUseItem(): ItemStack? {
        val useItem: Any? = LivingEntityAccessor.METHOD_GET_USE_ITEM!!.invoke(entity)
        if (useItem == null || useItem == NMSUtils.getNmsEmptyItemStack()) return null
        return NMSUtils.getBukkitItemStack(useItem)
    }

    fun collect(collectedEntityId: Int, amount: Int) {
        collect(collectedEntityId, entityId, amount)
    }

    /**
     * @apiNote > 1.20.6
     */
    fun setScale(scale: Double, viewer: Player) {
        val scaleAttribute = getScaleAttribute()
        val oldScale = AttributeInstanceAccessor.METHOD_GET_BASE_VALUE!!.invoke(scaleAttribute)
        AttributeInstanceAccessor.METHOD_SET_BASE_VALUE!!.invoke(scaleAttribute, scale)
        viewer.sendPacket(PacketUtils.getUpdateAttributesPacket(entityId, listOf(scaleAttribute)))
        AttributeInstanceAccessor.METHOD_SET_BASE_VALUE!!.invoke(scaleAttribute, oldScale)
    }

    /**
     * @apiNote > 1.20.6
     */
    fun setScale(scale: Double) {
        val scaleAttribute = getScaleAttribute()
        AttributeInstanceAccessor.METHOD_SET_BASE_VALUE!!.invoke(scaleAttribute, scale)
        getViewers().sendPacket(PacketUtils.getUpdateAttributesPacket(entityId, listOf(scaleAttribute)))
    }

    /**
     * @apiNote > 1.20.6
     */
    fun getScale(): Double {
        val scaleAttribute = getScaleAttribute()
        return AttributeInstanceAccessor.METHOD_GET_BASE_VALUE!!.invoke(scaleAttribute) as Double
    }

    internal fun getScaleAttribute(): Any = LivingEntityAccessor.METHOD_GET_ATTRIBUTE!!.invoke(entity, AttributesAccessor.FIELD_SCALE)

    override fun onPostAddViewers(vararg viewers: Player) {
        super.onPostAddViewers(*viewers)
        if (ServerVersion.isAtLeast(20, 6)) {
            viewers.sendPacket(PacketUtils.getUpdateAttributesPacket(entityId, listOf(getScaleAttribute())))
        }
    }

}