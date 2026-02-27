package org.sayandev.stickynote.paper.nms

import org.bukkit.entity.Player
import java.util.Collections

abstract class Viewable {

    private val viewers: MutableSet<Player> = Collections.synchronizedSet(HashSet())

    /**
     * Adds a viewer
     * @param viewer The viewer to add
     */
    protected abstract fun addViewer(viewer: Player)

    /**
     * Removes a viewer
     * @param viewer The viewer to remove
     */
    protected abstract fun removeViewer(viewer: Player)

    /**
     * Called before viewers are added
     */
    protected open fun onPreAddViewers(vararg viewers: Player) {
        //Optional event that can be overriden
    }

    /**
     * Called before viewers are removed
     */
    protected open fun onPreRemovePlayers(vararg viewers: Player) {
        //Optional event that can be overriden
    }

    /**
     * Called after viewers are added
     */
    protected open fun onPostAddViewers(vararg viewers: Player) {
        //Optional event that can be overriden
    }

    /**
     * Called after viewers are removed
     */
    protected open fun onPostRemoveViewers(vararg viewers: Player) {
        //Optional event that can be overriden
    }

    /**
     * Adds a collection of viewers
     * @param viewers The viewers to add
     */
    fun addViewers(vararg viewers: Player) {
        onPreAddViewers(*viewers)
        for (player in viewers) {
            addViewer(player)
            this.viewers.add(player)
        }
        onPostAddViewers(*viewers)
    }

    /**
     * Adds a collection of viewers
     * @param viewers The viewers to add
     */
    fun addViewers(viewers: Collection<Player>) {
        addViewers(*viewers.toTypedArray<Player>())
    }

    /**
     * Removes a collection of viewers
     * @param viewers The viewers to remove
     */
    fun removeViewers(vararg viewers: Player) {
        onPreRemovePlayers(*viewers)
        for (player in viewers) {
            removeViewer(player)
            this.viewers.remove(player)
        }
        onPostRemoveViewers(*viewers)
    }

    /**
     * Removes a collection of viewers
     * @param viewers The viewers to remove
     */
    fun removeViewers(viewers: Collection<Player>) {
        removeViewers(*viewers.toTypedArray<Player>())
    }

    /**
     * @return The mutable set of viewers
     */
    protected fun getMutableViewers(): MutableSet<Player> {
        return viewers
    }

    /**
     * @return An immutable set of viewers
     */
    fun getViewers(): Set<Player> {
        synchronized(viewers) {
            return HashSet(viewers)
        }
    }


}