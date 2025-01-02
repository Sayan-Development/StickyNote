package org.sayandev.stickynote.bukkit.nms.npc.entity.display

import com.cryptomorin.xseries.XMaterial
import org.bukkit.Location
import org.bukkit.Material
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.accessors.Display_BlockDisplayAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.SynchedEntityDataAccessor
import org.sayandev.stickynote.bukkit.nms.npc.NPCType
import org.sayandev.stickynote.bukkit.utils.ServerVersion

class BlockDisplayNPC(
    location: Location,
    material: XMaterial
): DisplayNPC(
    Display_BlockDisplayAccessor.CONSTRUCTOR_0!!.newInstance(NPCType.BLOCK_DISPLAY.nmsEntityType(), NMSUtils.getServerLevel(location.world)),
    location,
    NPCType.BLOCK_DISPLAY
) {

    var type: Material = Material.STONE
        set(value) {
            field = value

            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                Display_BlockDisplayAccessor.FIELD_DATA_BLOCK_STATE_ID,
                NMSUtils.getBlockState(value)
            )
        }

    init {
        type = material.parseMaterial() ?: throw IllegalArgumentException("Invalid material for version ${ServerVersion.completeVersion()}: $material")
    }

}