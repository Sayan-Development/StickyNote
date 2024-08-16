package org.sayandev.stickynote.bukkit.nms.npc.entity.display

import org.bukkit.Location
import org.joml.Quaternionf
import org.joml.Vector3f
import org.sayandev.stickynote.bukkit.nms.accessors.DisplayAccessor
import org.sayandev.stickynote.bukkit.nms.accessors.SynchedEntityDataAccessor
import org.sayandev.stickynote.bukkit.nms.npc.EntityNPC
import org.sayandev.stickynote.bukkit.nms.npc.NPCType
import org.sayandev.stickynote.core.math.Vector3

abstract class DisplayNPC(
    entity: Any,
    location: Location,
    type: NPCType
): EntityNPC(
    entity,
    location,
    type
) {

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

    var scale: Vector3 = Vector3.zero
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

    var brightnessOverride: Int = 0
        set(value) {
            field = value.coerceIn(-1, 255)
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_BRIGHTNESS_OVERRIDE_ID!!,
                field
            )
            sendEntityData()
        }

    var viewRange: Float = 0f
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

    var shadowStrength: Float = 0f
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

    var glowColorOverride: Int = 0
        set(value) {
            field = value
            SynchedEntityDataAccessor.METHOD_SET!!.invoke(
                getEntityData(),
                DisplayAccessor.FIELD_DATA_GLOW_COLOR_OVERRIDE_ID!!,
                value
            )
            sendEntityData()
        }

    init {
        defineDefaultValues()
    }

    private fun defineDefaultValues() {
        transformationInterpolationStartDeltaTicks = 0
        transformationInterpolationDuration = 0
        posRotInterpolationDuration = 0
        translation = Vector3.zero
        scale = Vector3.one
        leftRotation = Quaternionf()
        rightRotation = Quaternionf()
        billboardRenderConstraints = BillboardConstraints.FIXED
        brightnessOverride = -1
        viewRange = 1f
        shadowRadius = 0f
        shadowStrength = 1f
        width = 0f
        height = 0f
        glowColorOverride = -1
    }

}