package org.sayandev.stickynote.bukkit

import org.sayandev.stickynote.nms.accessors.ClientboundPlayerInfoUpdatePacketAccessor
import org.sayandev.stickynote.nms.accessors.ClientboundPlayerInfoUpdatePacket_ActionAccessor

object PacketUtils {

    init {
        ClientboundPlayerInfoUpdatePacket_ActionAccessor
    }

    enum class PlayerInfoAction(private val modernNmsObject: Any?, private val legacyNmsObject: Any?) {
        ADD_PLAYER(
            ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_ADD_PLAYER,
            ClientboundPlayerInfoPacket_ActionAccessor.getFieldADD_PLAYER()
        ),
        UPDATE_LISTED(ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_UPDATE_LISTED, null),
        UPDATE_GAME_MODE(
            ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_UPDATE_GAME_MODE,
            ClientboundPlayerInfoPacket_ActionAccessor.getFieldUPDATE_GAME_MODE()
        ),
        UPDATE_LATENCY(
            ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_UPDATE_LATENCY,
            ClientboundPlayerInfoPacket_ActionAccessor.getFieldUPDATE_LATENCY()
        ),
        UPDATE_DISPLAY_NAME(
            ClientboundPlayerInfoUpdatePacket_ActionAccessor.FIELD_UPDATE_DISPLAY_NAME,
            ClientboundPlayerInfoPacket_ActionAccessor.getFieldUPDATE_DISPLAY_NAME()
        ),
        REMOVE_PLAYER(null, ClientboundPlayerInfoPacket_ActionAccessor.getFieldREMOVE_PLAYER())
    }

    enum class ChatType(private val legacyId: Byte) {
        CHAT(0.toByte()),
        SYSTEM(1.toByte()),
        GAME_INFO(2.toByte());

        private val nmsObject: Any?
            get() {
                return when (this) {
                    CHAT -> ChatTypeAccessor.getFieldCHAT()
                    SYSTEM -> ChatTypeAccessor.getFieldSYSTEM()
                    GAME_INFO -> ChatTypeAccessor.getFieldGAME_INFO()
                }
                return null
            }
    }

    enum class NameTagVisibility(
        private val nmsName: String, //1.17 and above
        private val modernNmsObject: Any
    ) {
        ALWAYS("always", Team_VisibilityAccessor.getFieldALWAYS()),
        HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", Team_VisibilityAccessor.getFieldHIDE_FOR_OTHER_TEAMS()),
        HIDE_FOR_OWN_TEAM("hideForOwnTeam", Team_i_VisibilityAccessor.getFieldHIDE_FOR_OWN_TEAM()),
        NEVER("never", Team_VisibilityAccessor.getFieldNEVER());

        init {
            this.modernNmsObject = modernNmsObject
        }
    }

    enum class CollisionRule(
        private val nmsName: String,
        private val modernNmsObject: Any //1.17 and above
    ) {
        ALWAYS("always", Team_CollisionRuleAccessor.getFieldALWAYS()),
        PUSH_OTHER_TEAMS("pushOtherTeams", Team_CollisionRuleAccessor.getFieldPUSH_OTHER_TEAMS()),
        PUSH_OWN_TEAM("pushOwnTeam", Team_CollisionRuleAccessor.getFieldPUSH_OWN_TEAM()),
        NEVER("never", Team_CollisionRuleAccessor.getFieldNEVER());
    }

}