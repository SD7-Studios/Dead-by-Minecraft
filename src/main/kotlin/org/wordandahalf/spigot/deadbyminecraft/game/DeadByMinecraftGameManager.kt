package org.wordandahalf.spigot.deadbyminecraft.game

import org.wordandahalf.spigot.deadbyminecraft.config.DeadByMinecraftConfig
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import java.util.*

/**
 * Keeps track of running DeadByMinecraft games.
 */
object DeadByMinecraftGameManager
{
    private val games : TreeMap<Int, DeadByMinecraftGame> = TreeMap()
    private fun nextID() : Int
    {
        // If there are no games
        if(games.size == 0)
            return 0

        var previousID = 0
        for(entry in games.entries)
        {
            // If the two IDs are not consecutive
            if(entry.key != previousID + 1)
            {
                // Return the number one greater than the
                return previousID + 1
            }

            previousID = entry.key
        }

        // Otherwise return the next consecutive ID
        return games.lastKey() + 1
    }

    /**
     * Creates a new game
     * @return the ID of the new game
     */
    fun create() : Int
    {
        val id : Int = nextID()

        games[id] = DeadByMinecraftGame(id, DeadByMinecraftConfig.Main.maxPlayers)

        return id
    }

    /**
     * Removes a game with the provided ID
     * @return whether the game was removed
     */
    fun remove(id : Int) : Boolean
    {
        if(id > games.size - 1) return false else games[id]?.stop(); games.remove(id); return true
    }

    fun all() : Array<DeadByMinecraftGame>
    {
        return games.values.toTypedArray()
    }

    fun byID(id : Int) : DeadByMinecraftGame?
    {
        return games[id]
    }

    fun byPlayer(player : DeadByMinecraftPlayer) : DeadByMinecraftGame?
    {
        return games.values.firstOrNull { it.players.contains(player) }
    }
}