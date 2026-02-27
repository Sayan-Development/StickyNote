package org.sayandev.stickynote.paper.nms.enum

import org.sayandev.stickynote.paper.nms.accessors.ChatTypeAccessor

/**
 * @param legacyId The legacy id of the chat type.
 * @param nmsOject The nms object of the chat type.
 */
enum class ChatType(val legacyId: Byte, val nmsOject: Any?) {
    CHAT(0.toByte(), ChatTypeAccessor.FIELD_CHAT),
    SYSTEM(1.toByte(), ChatTypeAccessor.FIELD_SYSTEM),
    GAME_INFO(2.toByte(), ChatTypeAccessor.FIELD_GAME_INFO);
}