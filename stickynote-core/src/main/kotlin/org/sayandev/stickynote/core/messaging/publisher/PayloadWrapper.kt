package org.sayandev.stickynote.core.messaging.publisher

import java.util.UUID

data class PayloadWrapper<P>(
    val uniqueId: UUID,
    val payload: P
) {
    constructor(payload: P): this(UUID.randomUUID(), payload)
}