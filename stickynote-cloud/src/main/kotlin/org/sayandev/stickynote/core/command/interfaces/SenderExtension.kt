package org.sayandev.stickynote.core.command.interfaces

interface SenderExtension<S, P> {

    /**
     * Retrieves the player associated with the sender.
     *
     * @return The player if the sender is a player, otherwise null.
     */
    fun player(): P?

    /**
     * Sets the command sender for this sender instance.
     *
     * @param sender The command sender to set.
     */
    fun platformSender(sender: S)

    /**
     * Retrieves the command sender associated with this sender instance.
     *
     * @return The command sender associated with this sender instance.
     */
    fun platformSender(): S

    /**
     * Sends a message to the sender if they are not a player.
     *
     * @param message The message to send.
     */
    fun onlyPlayersComponent(message: String)
}
