package org.sayandev.stickynote.core.messaging

enum class PayloadBehaviour {
    /**
     * Forwards to all available subscribers.
     * Subscribers will handle the message and return a response if provided.
     * The result will only be available if one of the subscribers returns a response.
     * If none of the subscribers return a response, the payload will be ignored.
     */
    FORWARD,

    FORWARD_PROXY,

    /**
     * The payload will be sent to the source of the message.
     */
    RESPONSE
}