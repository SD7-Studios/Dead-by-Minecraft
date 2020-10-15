package org.wordandahalf.spigot.deadbyminecraft.game.player.`interface`

import org.wordandahalf.spigot.deadbyminecraft.scheduling.Disposable
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Tickable

/**
 * Represents an element of a player's interface
 */
interface DeadByMinecraftPlayerInterfaceElement<T> : Disposable, Tickable
{
    /**
     * Returns a form of this element that can be sent to the client, or null.
     * Returning null signifies to the [DeadByMinecraftPlayerInterface] that this element should be removed.
     */
    fun build() : T?
}