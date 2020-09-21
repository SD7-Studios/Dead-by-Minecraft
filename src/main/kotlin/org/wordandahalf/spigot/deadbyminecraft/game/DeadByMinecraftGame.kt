package org.wordandahalf.spigot.deadbyminecraft.game

import org.bukkit.persistence.PersistentDataType
import org.wordandahalf.spigot.deadbyminecraft.game.states.DeadByMinecraftGameState
import org.wordandahalf.spigot.deadbyminecraft.game.states.DeadByMinecraftLobbyState

class DeadByMinecraftGame(val id: Int, val maxPlayers: Int)
{
    private val players : ArrayList<DeadByMinecraftPlayer> = ArrayList(maxPlayers)

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

    fun getPlayers() : List<DeadByMinecraftPlayer>
    {
        return players
    }

    fun addPlayer(player: DeadByMinecraftPlayer) : Boolean
    {
        if(!players.contains(player))
        {
            state.onPlayerJoin(player)
            sendMessage(player.bukkit.displayName + " has joined the game!")
            players.add(player)
            player.set("game_id", PersistentDataType.INTEGER, id)
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
            player.remove("game_id")
            it.remove()
        }
    }

    fun removePlayer(player: DeadByMinecraftPlayer) : Boolean
    {
        if(players.contains(player))
        {
            state.onPlayerLeave(player)
            player.remove("game_id")
            players.remove(player)
            sendMessage(player.bukkit.displayName + " has left the game!")
            return true
        }

        return false
    }

    fun getNumberOfPlayers() : Int { return players.size }

    fun sendMessage(message: String)
    {
        players.forEach {
            it.bukkit.sendRawMessage(message)
        }
    }
}