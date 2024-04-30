package org.sayandevelopment.stickynote.bukkit.command.interfaces

import net.kyori.adventure.text.Component
import org.sayandevelopment.stickynote.bukkit.StickyNote

/**
 * Interface for defining commands.
 */
interface CommandExtension {

    /**
     * Retrieves the permission associated with the specified command literal.
     *
     * @param literal The command literal.
     * @return The permission string.
     */
    fun constructBasePermission(literal: String): String {
        return "${StickyNote.plugin().name}.commands.$literal"
    }

    fun errorPrefix(): Component

    /**
     * Sets the prefix for error messages related to this command.
     *
     * @param prefix The error prefix to set.
     */
    fun errorPrefix(prefix: Component)

}
