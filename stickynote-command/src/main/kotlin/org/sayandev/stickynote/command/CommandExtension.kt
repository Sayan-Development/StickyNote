package org.sayandev.stickynote.command

interface CommandExtension {
    fun register()

    fun unregister() {
    }
}
