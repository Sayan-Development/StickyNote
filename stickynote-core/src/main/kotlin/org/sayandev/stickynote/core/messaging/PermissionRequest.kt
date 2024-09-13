package org.sayandev.stickynote.core.messaging

import java.util.UUID

data class PermissionRequest(
    val player: UUID,
    val permission: String
)