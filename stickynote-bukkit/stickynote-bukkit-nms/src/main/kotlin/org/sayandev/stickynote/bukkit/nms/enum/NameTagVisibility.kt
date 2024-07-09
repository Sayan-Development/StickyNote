package org.sayandev.stickynote.bukkit.nms.enum

import org.sayandev.stickynote.nms.accessors.Team_VisibilityAccessor

/**
 * @param nmsName The name of the name tag visibility. Used for 1.16 and below.
 * @param modernNmsObject The nms object of the name tag visibility. Used for 1.17 and above.
 */
enum class NameTagVisibility(
    val nmsName: String,
    val modernNmsObject: Any?
) {
    ALWAYS("always", Team_VisibilityAccessor.FIELD_ALWAYS),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", Team_VisibilityAccessor.FIELD_HIDE_FOR_OTHER_TEAMS),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam", Team_VisibilityAccessor.FIELD_HIDE_FOR_OWN_TEAM),
    NEVER("never", Team_VisibilityAccessor.FIELD_NEVER);
}