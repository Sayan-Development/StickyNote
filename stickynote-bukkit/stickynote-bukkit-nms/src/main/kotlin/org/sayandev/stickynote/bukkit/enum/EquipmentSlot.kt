package org.sayandev.stickynote.bukkit.enum

import org.sayandev.stickynote.nms.accessors.EquipmentSlotAccessor

enum class EquipmentSlot(val nmsSlot: Any) {
    MAINHAND(EquipmentSlotAccessor.FIELD_MAINHAND!!),
    OFFHAND(EquipmentSlotAccessor.FIELD_OFFHAND!!),
    HEAD(EquipmentSlotAccessor.FIELD_HEAD!!),
    CHEST(EquipmentSlotAccessor.FIELD_CHEST!!),
    FEET(EquipmentSlotAccessor.FIELD_FEET!!),
    LEGS(EquipmentSlotAccessor.FIELD_LEGS!!)
}