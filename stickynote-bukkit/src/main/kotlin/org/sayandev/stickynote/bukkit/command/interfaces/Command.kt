package org.sayandev.stickynote.bukkit.command.interfaces

import net.kyori.adventure.text.Component

/**
 * Interface for defining commands.
 */
interface CommandExtension {

    fun errorPrefix(): Component

    /**
     * Sets the prefix for error messages related to this command.
     *
     * @param prefix The error prefix to set.
     */
    fun errorPrefix(prefix: Component)

}
