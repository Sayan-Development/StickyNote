package org.sayandev.stickynote.bukkit.nms.hologram

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.Location
import org.bukkit.entity.Player
import org.sayandev.stickynote.bukkit.extension.toVector3
import org.sayandev.stickynote.bukkit.nms.Viewable
import org.sayandev.stickynote.core.math.Vector3
import java.util.UUID

class Hologram(
    lines: List<HoloLine>,
    private var location: Location
): Viewable() {

    private val originalLines = lines
    private val lines = originalLines.toMutableList()
    private val lineLocations: MutableMap<UUID, Location> = mutableMapOf()

    init {
        reload()
    }

    private fun reload(lines: List<HoloLine>, location: Location) {
        val previousViewers = getViewers().toList()
        unload()
        val newLines = lines.toMutableList()
        this.lines.clear()
        this.lineLocations.clear()

        val suitableLocation = location.clone()
        for ((lineIndex, line) in newLines.withIndex()) {
            if (lineIndex > 0) {
                suitableLocation.subtract(0.0, line.distance.toDouble(), 0.0)
            }
            lineLocations[line.uniqueId] = suitableLocation

            line.initializeNPC(suitableLocation)

            this.lines.add(line)
        }
        previousViewers.forEach(this::addViewer)
    }

    /**
     * Reloads the hologram
     */
    fun reload() {
        reload(lines, location)
    }

    /**
     * Unloads the hologram
     */
    fun unload() {
        unload(*getOnlinePlayers().toTypedArray())
    }

    fun unload(vararg players: Player) {
        for (line in lines) {
            if (line.isInitialized()) {
                line.npc.removeViewers(*players)
            }
        }
    }

    fun location(): Location {
        return location
    }

    /**
     * Moves the hologram by a vector
     * @param vector3 Vector to move the hologram by. Length of the vector shouldn't be greater than 8
     */
    fun move(vector3: Vector3) {
        location.add(vector3.x, vector3.y, vector3.z)
        for (line in lines) {
            line.npc.move(vector3)
        }
    }

    /**
     * Teleports the hologram to a location
     * @param location Location to teleport the hologram to
     */
    fun teleport(location: Location) {
        this.location = location
        for (line in lines) {
            val lineLocation: Location = lineLocations[line.uniqueId]!!
            line.npc.teleport(lineLocation.toVector3())
        }
        reload()
    }

    /**
     * Sets the lines of the hologram. This will reload the hologram
     * @param lines List of lines
     */
    fun lines(lines: List<HoloLine>) {
        reload(lines, location)
    }

    /**
     * Sets the line at the specified index. This will reload the hologram
     * @param index Index of the line
     * @param line Line to set
     * @return false if the index is out of bounds
     */
    fun setLine(index: Int, line: HoloLine): Boolean {
        if (index < 0 || index >= lines.size) return false
        lines[index] = line

        reload()
        return true
    }

    /**
     * Sets the component of the line at the specified index. This will reload the hologram
     * @param index Index of the line
     * @param newComponent New component to set
     * @return false if the index is out of bounds or the line is not a HologramLine
     * @see HologramLine
     */
    fun setLine(index: Int, newComponent: Component): Boolean {
        if (index < 0 || index >= lines.size) return false
        if (lines[index] !is HologramLine) return false

        (lines[index] as HologramLine).component = newComponent
        return true
    }

    /**
     * Adds a line to the hologram. This will reload the hologram
     * @param line Line to add
     */
    fun addLine(line: HoloLine) {
        lines.add(line)

        reload()
    }

    /**
     * Removes the line at the specified index. This will reload the hologram
     * @param index Index of the line
     * @return false if the index is out of bounds
     */
    fun removeLine(index: Int): Boolean {
        if (index < 0 || index >= lines.size) return false
        lines[index].npc.removeViewers(getOnlinePlayers())
        lines.removeAt(index)

        reload()
        return true
    }

    /**
     * Returns an immutable list of lines
     * @return Immutable list of lines
     */
    fun lines(): List<HoloLine> {
        return lines.toList()
    }

    fun originalLines(): List<HoloLine> {
        return originalLines
    }

    /**
     * Adds a viewer
     * @param viewer The viewer to add
     */
    override fun addViewer(viewer: Player) {
        for (line in lines) {
            if (line.isInitialized()) {
                line.npc.addViewers(viewer)
            }
        }
    }

    /**
     * Removes a viewer
     * @param viewer The viewer to remove
     */
    override fun removeViewer(viewer: Player) {
        for (line in lines) {
            if (line.isInitialized()) {
                line.npc.removeViewers(viewer)
            }
        }
    }

}