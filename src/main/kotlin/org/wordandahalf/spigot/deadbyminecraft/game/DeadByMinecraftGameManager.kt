package org.wordandahalf.spigot.deadbyminecraft.game

import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraftPlugin
import java.util.*

// Kotlin does not offer a static modifier; thus an object expression is required
object DeadByMinecraftGameManager
{
    private val games : TreeMap<Int, DeadByMinecraftGame> = TreeMap()
    private fun getNextGameID() : Int
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
    fun createGame() : Int
    {
        val id : Int = getNextGameID()

        games[id] = DeadByMinecraftGame(id, DeadByMinecraftPlugin.Config.maxPlayers())

        return id
    }

    /**
     * Removes a game with the provided ID
     * @return whether the game was removed
     */
    fun removeGame(id : Int) : Boolean
    {
        if(id > games.size - 1) return false else games[id]?.stop(); games.remove(id); return true
    }

    fun getGames() : Array<DeadByMinecraftGame>
    {
        return games.values.toTypedArray()
    }

    fun getGameByID(id : Int) : DeadByMinecraftGame?
    {
        return games[id]
    }

    fun getGameByPlayer(player : DeadByMinecraftPlayer) : DeadByMinecraftGame?
    {
        TODO()
    }
}