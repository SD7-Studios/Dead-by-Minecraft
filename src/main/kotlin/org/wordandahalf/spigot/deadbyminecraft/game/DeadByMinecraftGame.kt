package org.wordandahalf.spigot.deadbyminecraft.game

import com.grinderwolf.swm.api.world.SlimeWorld
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.World
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraftWorlds
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.killer.DeadByMinecraftKillerRole
import org.wordandahalf.spigot.deadbyminecraft.game.states.DeadByMinecraftGameState
import org.wordandahalf.spigot.deadbyminecraft.game.states.DeadByMinecraftLobbyState

/**
 * Represents a game of Dead by Minecraft and all associated data.
 */
class DeadByMinecraftGame(val id: Int, val maxPlayers: Int)
{
    val players : ArrayList<DeadByMinecraftPlayer> = ArrayList(maxPlayers)

    private val lobbyWorld : SlimeWorld = DeadByMinecraftWorlds.cloneLobby()
    private val gameWorld : SlimeWorld = DeadByMinecraftWorlds.cloneGame()

    var state : DeadByMinecraftGameState = DeadByMinecraftLobbyState(this)
        set(newState)
        {
            field.onLeave()
            field = newState
        }

    fun stop()
    {
        state.onLeave()
        removeAllPlayers()
    }

    //
    // Player management functions
    //

    fun addPlayer(player: DeadByMinecraftPlayer) : Boolean
    {
        if(!players.contains(player))
        {
            state.onPlayerJoin(player)
            sendMessage(player.bukkit.displayName + " has joined the game!")
            players.add(player)
            player.data.gameID = id
            // player.saveData()
            return true
        }

        return false
    }

    fun removeAllPlayers()
    {
        val it = players.iterator()
        while(it.hasNext())
        {
            val player = it.next()

            state.onPlayerLeave(player)
            player.data.gameID = null
            // player.deleteData()
            it.remove()
        }
    }

    fun removePlayer(player: DeadByMinecraftPlayer) : Boolean
    {
        if(players.contains(player))
        {
            state.onPlayerLeave(player)
            sendMessage(player.bukkit.displayName + " has left the game!")
            player.data.gameID = null
            // player.deleteData()
            players.remove(player)
            return true
        }

        return false
    }

    fun hasKiller() : Boolean
    {
        return players.filter { it.data.role is DeadByMinecraftKillerRole }.isNotEmpty()
    }

    fun numberOfPlayers() : Int { return players.size }

    //
    // World functions
    //

    fun bukkitLobbyWorld() : World { return Bukkit.getWorld(lobbyWorld.name)!! }
    fun bukkitGameWorld() : World { return Bukkit.getWorld(gameWorld.name)!! }

    fun sendMessage(message: String)
    {
        players.forEach {
            it.sendMessage(ChatMessageType.ACTION_BAR, *ComponentBuilder(message).color(ChatColor.YELLOW).create())
        }
    }
}