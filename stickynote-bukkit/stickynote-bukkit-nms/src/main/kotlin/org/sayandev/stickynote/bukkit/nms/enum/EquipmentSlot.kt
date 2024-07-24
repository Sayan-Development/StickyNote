package org.sayandev.stickynote.bukkit.nms.enum

import org.sayandev.stickynote.nms.accessors.EquipmentSlotAccessor

enum class EquipmentSlot(val bukkitSlot: org.bukkit.inventory.EquipmentSlot, val nmsSlot: Any) {
    MAINHAND(org.bukkit.inventory.EquipmentSlot.HAND, EquipmentSlotAccessor.FIELD_MAINHAND!!),
    OFFHAND(org.bukkit.inventory.EquipmentSlot.OFF_HAND, EquipmentSlotAccessor.FIELD_OFFHAND!!),
    HEAD(org.bukkit.inventory.EquipmentSlot.HEAD, EquipmentSlotAccessor.FIELD_HEAD!!),
    CHEST(org.bukkit.inventory.EquipmentSlot.CHEST, EquipmentSlotAccessor.FIELD_CHEST!!),
    FEET(org.bukkit.inventory.EquipmentSlot.FEET, EquipmentSlotAccessor.FIELD_FEET!!),
    LEGS(org.bukkit.inventory.EquipmentSlot.LEGS, EquipmentSlotAccessor.FIELD_LEGS!!)
}