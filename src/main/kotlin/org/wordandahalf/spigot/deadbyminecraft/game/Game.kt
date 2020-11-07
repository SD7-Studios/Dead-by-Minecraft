package org.wordandahalf.spigot.deadbyminecraft.game

import org.wordandahalf.spigot.deadbyminecraft.actions.Actions
import org.wordandahalf.spigot.deadbyminecraft.actions.PlayerJoinAction
import org.wordandahalf.spigot.deadbyminecraft.actions.PlayerLeaveAction
import org.wordandahalf.spigot.deadbyminecraft.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.player.roles.killer.KillerRole
import org.wordandahalf.spigot.deadbyminecraft.worlds.Worlds

/**
 * Represents a game of Dead by Minecraft and all associated data.
 */
class Game(val id: Int, val maxPlayers: Int)
{
    val players : ArrayList<DeadByMinecraftPlayer> = ArrayList(maxPlayers)

    val lobbyWorld = Worlds.cloneLobby()
    val gameWorld = Worlds.cloneGame()

    fun stop()
    {
        val it = players.iterator()
        while(it.hasNext())
        {
            val player = it.next()

            Actions.submit(PlayerLeaveAction(player.bukkit.world, player))
            player.data.gameID = null

            it.remove()
        }

        lobbyWorld.dispose()
        gameWorld.dispose()
    }

    fun addPlayer(player: DeadByMinecraftPlayer) : Boolean
    {
        if(!players.contains(player))
        {
            players.add(player)
            player.data.gameID = id
            Actions.submit(PlayerJoinAction(lobbyWorld.bukkit, player))

            return true
        }

        return false
    }

    fun removePlayer(player: DeadByMinecraftPlayer) : Boolean
    {
        if(players.contains(player))
        {
            Actions.submit(PlayerLeaveAction(player.bukkit.world, player))

            player.data.gameID = null
            players.remove(player)

            return true
        }

        return false
    }

    fun getKillerRatio() : Double
    {
        return players.count { it.data.role is KillerRole }.toDouble() / players.size.toDouble()
    }

    fun numberOfPlayers() : Int { return players.size }
}