package org.sayandev.stickynote.bukkit.nms.npc.entity.display

import org.bukkit.Location
import org.joml.Quaternionf
import org.joml.Vector3f
import org.sayandev.stickynote.bukkit.nms.NMSUtils
import org.sayandev.stickynote.bukkit.nms.accessors.DisplayAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.SynchedEntityDataAccessor
import org.sayandev.stickynote.bukkit.nms.npc.EntityNPC
import org.sayandev.stickynote.bukkit.nms.npc.NPCType
import org.sayandev.stickynote.core.math.Vector3

abstract class DisplayNPC(
    location: Location,
    type: NPCType
): EntityNPC(
    DisplayAccessor.CONSTRUCTOR_0!!.newInstance(type.nmsEntityType, NMSUtils.getServerLevel(location.world)),
    location,
    type
) {

    init {
        DisplayAccessor.METHOD_DEFINE_SYNCHED_DATA!!.invoke(entity)
    }

    var transformationInterpolationStartDeltaTicks: Int = 0
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID!!,
                value
            )
            sendEntityData()
        }

    var transformationInterpolationDuration: Int = 0
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID!!,
                value
            )
            sendEntityData()
        }

    var posRotInterpolationDuration: Int = 0
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_POS_ROT_INTERPOLATION_DURATION_ID!!,
                value
            )
            sendEntityData()
        }

    var translation: Vector3 = Vector3.zero
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_TRANSLATION_ID!!,
                Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
            )
            sendEntityData()
        }

    var scale: Vector3 = Vector3.one
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_SCALE_ID!!,
                Vector3f(value.x.toFloat(), value.y.toFloat(), value.z.toFloat())
            )
            sendEntityData()
        }

    var leftRotation: Quaternionf = Quaternionf()
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_LEFT_ROTATION_ID!!,
                value
            )
            sendEntityData()
        }

    var rightRotation: Quaternionf = Quaternionf()
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_RIGHT_ROTATION_ID!!,
                value
            )
            sendEntityData()
        }

    var billboardRenderConstraints: BillboardConstraints = BillboardConstraints.FIXED
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_BILLBOARD_RENDER_CONSTRAINTS_ID!!,
                value.getId()
            )
            sendEntityData()
        }

    var brightnessOverride: Int = -1
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_BRIGHTNESS_OVERRIDE_ID!!,
                value
            )
            sendEntityData()
        }

    var viewRange: Float = 1f
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_VIEW_RANGE_ID!!,
                value
            )
            sendEntityData()
        }

    var shadowRadius: Float = 0f
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_SHADOW_RADIUS_ID!!,
                value
            )
            sendEntityData()
        }

    var shadowStrength: Float = 1f
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_SHADOW_STRENGTH_ID!!,
                value
            )
            sendEntityData()
        }

    var width: Float = 0f
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_WIDTH_ID!!,
                value
            )
            sendEntityData()
        }

    var height: Float = 0f
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_HEIGHT_ID!!,
                value
            )
            sendEntityData()
        }

    var glowColorOverride: Int = -1
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_GLOW_COLOR_OVERRIDE_ID!!,
                value
            )
            sendEntityData()
        }

}