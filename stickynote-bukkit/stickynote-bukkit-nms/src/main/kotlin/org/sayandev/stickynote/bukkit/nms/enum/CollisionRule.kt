package org.sayandev.stickynote.bukkit.nms.enum

import org.sayandev.stickynote.bukkit.nms.accessors.Team_CollisionRuleAccessor

/**
 * @param nmsName The name of the collision rule. Used for 1.16 and below.
 * @param modernNmsObject The nms object of the collision rule. Used for 1.17 and above.
 */
enum class CollisionRule(
    val nmsName: String,
    val modernNmsObject: Any?
) {
    ALWAYS("always", Team_CollisionRuleAccessor.FIELD_ALWAYS),
    PUSH_OTHER_TEAMS("pushOtherTeams", Team_CollisionRuleAccessor.FIELD_PUSH_OTHER_TEAMS),
    PUSH_OWN_TEAM("pushOwnTeam", Team_CollisionRuleAccessor.FIELD_PUSH_OWN_TEAM),
    NEVER("never", Team_CollisionRuleAccessor.FIELD_NEVER);
}