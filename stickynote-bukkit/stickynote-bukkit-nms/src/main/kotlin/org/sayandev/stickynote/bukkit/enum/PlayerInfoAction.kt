package org.sayandev.stickynote.bukkit.enum

import org.sayandev.stickynote.nms.accessors.ClientboundPlayerInfoUpdatePacket_ActionAccessor

/**
 * @param nmsObject The nms object of the PlayerInfoAction.
 */
enum class PlayerInfoAction(val nmsObject: Any?) {
    ADD_PLAYER(ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_ADD_PLAYER),
    UPDATE_LISTED(ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_UPDATE_LISTED),
    UPDATE_GAME_MODE(ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_UPDATE_GAME_MODE),
    UPDATE_LATENCY(ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_UPDATE_LATENCY),
    UPDATE_DISPLAY_NAME(ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_UPDATE_DISPLAY_NAME),
    REMOVE_PLAYER(ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_REMOVE_PLAYER)
}