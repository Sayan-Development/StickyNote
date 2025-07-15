package org.sayandev.stickynote.core.messaging.websocket

import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.handshake.ServerHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.net.URI

class MessageWebSocketServer(
    uri: URI
) : WebSocketServer(InetSocketAddress(uri.host, uri.port)) {
    override fun onOpen(connection: WebSocket, handshake: ClientHandshake) {
    }

    override fun onClose(connection: WebSocket, code: Int, reason: String, remote: Boolean) {
        // TODO: throw exception or something?
    }

    override fun onMessage(connection: WebSocket, message: String) {
        broadcast(message)
    }

    override fun onError(connection: WebSocket, e: Exception) {
    }

    override fun onStart() {
    }

    companion object {
        fun isWebSocketAvailable(uri: URI): Boolean {
            var available = false
            val client = object : WebSocketClient(uri) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    available = true
                    close()
                }
                override fun onMessage(message: String?) {}
                override fun onClose(code: Int, reason: String?, remote: Boolean) {}
                override fun onError(ex: Exception?) {}
            }
            try {
                client.connectBlocking()
            } catch (e: Exception) {
                return false
            }
            return available
        }

        fun getWebSocketServer(uri: URI): MessageWebSocketServer? {
            return if (isWebSocketAvailable(uri)) {
                MessageWebSocketServer(uri)
            } else {
                null
            }
        }
    }
}