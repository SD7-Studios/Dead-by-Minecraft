package org.wordandahalf.spigot.deadbyminecraft.game

import com.grinderwolf.swm.api.world.SlimeWorld
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.World
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraftPlugin
import org.wordandahalf.spigot.deadbyminecraft.game.states.DeadByMinecraftGameState
import org.wordandahalf.spigot.deadbyminecraft.game.states.DeadByMinecraftLobbyState

class DeadByMinecraftGame(val id: Int, val maxPlayers: Int)
{
    val players : ArrayList<DeadByMinecraftPlayer> = ArrayList(maxPlayers)

    private val lobbyWorld : SlimeWorld = DeadByMinecraftPlugin.Worlds.cloneLobbyWorld()
    private val gameWorld : SlimeWorld = DeadByMinecraftPlugin.Worlds.cloneGameWorld()

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
            player.data.save()
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
            player.data.delete()
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
            player.data.delete()
            players.remove(player)
            return true
        }

        return false
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