package org.sayandev.stickynote.velocity.messaging

import kotlinx.coroutines.CompletableDeferred
import org.sayandev.stickynote.core.messaging.PermissionRequest
import org.sayandev.stickynote.core.messaging.subscriber.Subscriber
import org.sayandev.stickynote.velocity.server

class PermissionRequestSubscriber: Subscriber<PermissionRequest, Boolean>(
    "permission_request",
    PermissionRequest::class.java
) {

    override fun onSubscribe(payload: PermissionRequest): CompletableDeferred<Boolean> {
        TODO()
    }

}