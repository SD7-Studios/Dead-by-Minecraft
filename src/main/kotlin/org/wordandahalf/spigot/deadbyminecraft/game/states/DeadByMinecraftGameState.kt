package org.wordandahalf.spigot.deadbyminecraft.game.states

import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGame
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Tickable

abstract class DeadByMinecraftGameState(val game: DeadByMinecraftGame)
{
    init
    {
        onEnter()
    }

    /**
     * Called when the parent DeadByMinecraftGame switches to this state, after #onLeave()
     *
     *
     * Cannot depend on state
     */
    abstract fun onEnter()

    abstract fun onPlayerJoin(player : DeadByMinecraftPlayer)
    abstract fun onPlayerLeave(player : DeadByMinecraftPlayer)

    /**
     * Called when the parent DeadByMinecraftGame switches to another state, before the next state's #onEnter()
     */
    abstract fun onLeave()

    abstract override fun toString(): String
}