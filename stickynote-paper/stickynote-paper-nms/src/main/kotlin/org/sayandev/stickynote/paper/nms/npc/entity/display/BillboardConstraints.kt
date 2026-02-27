package org.sayandev.stickynote.paper.nms.npc.entity.display

import org.sayandev.stickynote.paper.nms.accessors.Display_BillboardConstraintsAccessor

enum class BillboardConstraints(val nmsObject: Any) {
    FIXED(Display_BillboardConstraintsAccessor.FIELD_FIXED!!),
    VERTICAL(Display_BillboardConstraintsAccessor.FIELD_VERTICAL!!),
    HORIZONTAL(Display_BillboardConstraintsAccessor.FIELD_HORIZONTAL!!),
    CENTER(Display_BillboardConstraintsAccessor.FIELD_CENTER!!);

    fun getId(): Byte {
        return Display_BillboardConstraintsAccessor.METHOD_GET_ID!!.invoke(nmsObject) as Byte
    }
}