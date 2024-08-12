package org.sayandev.stickynote.bukkit.nms.npc.entity.display

import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.sayandev.stickynote.bukkit.nms.accessors.Display_TextDisplayAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.SynchedEntityDataAccessor
import org.sayandev.stickynote.bukkit.nms.npc.NPCType

class TextDisplayNPC(
    location: Location,
    text: Component
): DisplayNPC(
    location,
    NPCType.TEXT_DISPLAY
) {

    var text: Component = text
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_TextDisplayAccessor.FIELD_DATA_TEXT_ID,
                value
            )
            sendEntityData()
        }

    var lineWidth: Int = 200
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_TextDisplayAccessor.FIELD_DATA_LINE_WIDTH_ID,
                value
            )
            sendEntityData()
        }

    var backgroundColor: Int = 1073741824
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_TextDisplayAccessor.FIELD_DATA_BACKGROUND_COLOR_ID,
                value
            )
            sendEntityData()
        }

    var textOpacity: Byte = -1
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_TextDisplayAccessor.FIELD_DATA_TEXT_OPACITY_ID,
                value
            )
            sendEntityData()
        }

    var styleFlags: Byte = 0
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_TextDisplayAccessor.FIELD_DATA_STYLE_FLAGS_ID,
                value
            )
            sendEntityData()
        }

}