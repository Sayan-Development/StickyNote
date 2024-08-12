package org.sayandev.stickynote.bukkit.nms.enum

import org.sayandev.stickynote.bukkit.nms.accessors.ItemDisplayContextAccessor

enum class ItemDisplayContext(val nmsObject: Any) {
    NONE(ItemDisplayContextAccessor.FIELD_NONE!!),
    THIRD_PERSON_LEFT_HAND(ItemDisplayContextAccessor.FIELD_THIRD_PERSON_LEFT_HAND!!),
    THIRD_PERSON_RIGHT_HAND(ItemDisplayContextAccessor.FIELD_THIRD_PERSON_RIGHT_HAND!!),
    FIRST_PERSON_LEFT_HAND(ItemDisplayContextAccessor.FIELD_FIRST_PERSON_LEFT_HAND!!),
    FIRST_PERSON_RIGHT_HAND(ItemDisplayContextAccessor.FIELD_FIRST_PERSON_RIGHT_HAND!!),
    HEAD(ItemDisplayContextAccessor.FIELD_HEAD!!),
    GUI(ItemDisplayContextAccessor.FIELD_GUI!!),
    GROUND(ItemDisplayContextAccessor.FIELD_GROUND!!),
    FIXED(ItemDisplayContextAccessor.FIELD_FIXED!!);

    fun getId(): Byte {
        return ItemDisplayContextAccessor.METHOD_GET_ID!!.invoke(nmsObject) as Byte
    }
}