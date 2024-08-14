package org.sayandev.stickynote.bukkit.nms.npc.entity.display

import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.accessors.Display_TextDisplayAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.SynchedEntityDataAccessor
import org.sayandev.stickynote.bukkit.nms.npc.NPCType

class TextDisplayNPC(
    location: Location,
    display: Component
): DisplayNPC(
    Display_TextDisplayAccessor.CONSTRUCTOR_0!!.newInstance(NPCType.TEXT_DISPLAY.nmsEntityType, NMSUtils.getServerLevel(location.world)),
    location,
    NPCType.TEXT_DISPLAY
) {

    var text: Component = Component.empty()
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_TextDisplayAccessor.FIELD_DATA_TEXT_ID,
                MinecraftComponentSerializer.get().serialize(value)
            )
            sendEntityData()
        }

    var lineWidth: Int = 0
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_TextDisplayAccessor.FIELD_DATA_LINE_WIDTH_ID,
                value
            )
            sendEntityData()
        }

    var backgroundColor: Int = 0
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_TextDisplayAccessor.FIELD_DATA_BACKGROUND_COLOR_ID,
                value
            )
            sendEntityData()
        }

    var textOpacity: Byte = 0
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

    init {
        text = display
        defineDefaultValues()
    }

    fun setStyleFlags(vararg flags: StyleFlag) {
        styleFlags = StyleFlag.getMasks(*flags)
    }

    private fun defineDefaultValues() {
        lineWidth = 200
        backgroundColor = 1073741824
        textOpacity = -1
    }

    companion object {
        enum class StyleFlag(val mask: Byte) {
            HAS_SHADOW(0x01),
            SEE_THROUGH(0x02),
            USE_DEFAULT_BACKGROUND(0x04),
            ALIGN_LEFT(0x08),
            ALIGN_RIGHT(0x10);

            companion object {
                fun getMasks(vararg parts: StyleFlag): Byte {
                    var bytes: Byte = 0
                    for (part in parts) {
                        bytes = (bytes + part.mask).toByte()
                    }
                    return bytes
                }

                val allBitMasks: Byte = entries.fold(0.toByte()) { acc, styleFlag -> (acc + styleFlag.mask).toByte() }
            }
        }
    }

}