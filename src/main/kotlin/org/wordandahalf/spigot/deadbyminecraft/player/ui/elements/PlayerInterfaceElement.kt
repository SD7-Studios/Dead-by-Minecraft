package org.wordandahalf.spigot.deadbyminecraft.player.ui.elements

import org.wordandahalf.spigot.deadbyminecraft.scheduling.Disposable
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Tickable

/**
 * Represents an element of a player's interface
 */
interface PlayerInterfaceElement<T> : Disposable, Tickable
{
    /**
     * Returns a form of this element that can be sent to the client, or null.
     * Returning null signifies to the [DeadByMinecraftPlayerInterface] that this element should be removed.
     */
    fun build() : T?
}