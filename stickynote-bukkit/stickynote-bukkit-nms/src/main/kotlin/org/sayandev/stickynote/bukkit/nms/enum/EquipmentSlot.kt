package org.sayandev.stickynote.bukkit.nms.enum

import org.sayandev.stickynote.bukkit.nms.accessors.EquipmentSlotAccessor

enum class EquipmentSlot {
    MAINHAND,
    OFFHAND,
    HEAD,
    CHEST,
    FEET,
    LEGS;

    fun bukkitSlot(): org.bukkit.inventory.EquipmentSlot {
        return when (this) {
            MAINHAND -> org.bukkit.inventory.EquipmentSlot.HAND
            OFFHAND -> org.bukkit.inventory.EquipmentSlot.OFF_HAND
            HEAD -> org.bukkit.inventory.EquipmentSlot.HEAD
            CHEST -> org.bukkit.inventory.EquipmentSlot.CHEST
            FEET -> org.bukkit.inventory.EquipmentSlot.FEET
            LEGS -> org.bukkit.inventory.EquipmentSlot.LEGS
        }
    }

    fun nmsSlot(): Any {
        return when (this) {
            MAINHAND -> EquipmentSlotAccessor.FIELD_MAINHAND!!
            OFFHAND -> EquipmentSlotAccessor.FIELD_OFFHAND!!
            HEAD -> EquipmentSlotAccessor.FIELD_HEAD!!
            CHEST -> EquipmentSlotAccessor.FIELD_CHEST!!
            FEET -> EquipmentSlotAccessor.FIELD_FEET!!
            LEGS -> EquipmentSlotAccessor.FIELD_LEGS!!
        }
    }

    fun legacyNmsSlot(): Int {
        return when (this) {
            MAINHAND -> 0
            OFFHAND -> throw UnsupportedOperationException("Offhand is not supported in legacy versions")
            HEAD -> 4
            CHEST -> 3
            FEET -> 1
            LEGS -> 2
        }
    }
}