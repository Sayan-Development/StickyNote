package org.sayandev.stickynote.bukkit.nms.enum

import org.sayandev.stickynote.nms.accessors.InteractionHandAccessor

enum class InteractionHand(private val nmsObject: Any) {
    MAIN_HAND(InteractionHandAccessor.FIELD_MAIN_HAND!!),
    OFF_HAND(InteractionHandAccessor.FIELD_OFF_HAND!!);

    companion object {
        fun fromNmsObject(nmsInteractionHand: Any): InteractionHand? {
            return when (nmsInteractionHand) {
                InteractionHandAccessor.FIELD_MAIN_HAND!! -> MAIN_HAND
                InteractionHandAccessor.FIELD_OFF_HAND!! -> OFF_HAND
                else -> null
            }
        }
    }
}