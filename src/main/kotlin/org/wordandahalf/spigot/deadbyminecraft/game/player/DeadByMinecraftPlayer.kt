package org.wordandahalf.spigot.deadbyminecraft.game.player

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.entity.Player
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGame
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGameManager
import org.wordandahalf.spigot.deadbyminecraft.game.player.roles.DeadByMinecraftPlayerRole

/**
 * Wrapper for the Bukkit Player class, providing methods for saving and deleting DBM-related data.
 */
class DeadByMinecraftPlayer private constructor(val bukkit: Player)
{
    val data = Data()

    /**
     * Object for representing persistent data of a player
     */
    class Data
    {
        var gameID : Int? = null
        var role : DeadByMinecraftPlayerRole? = null

        fun getGame() : DeadByMinecraftGame? { return DeadByMinecraftGameManager.getGameByID(gameID ?: return null) }
    }

    companion object
    {
        private val playerCache : HashMap<Player, DeadByMinecraftPlayer> = hashMapOf()

        /**
         * Returns the Bukkit Player's respective cached DeadByMinecraftPlayer, creating a new one if it does not already exist.
         */
        fun of(player: Player) : DeadByMinecraftPlayer
        {
            if(playerCache[player] !is DeadByMinecraftPlayer)
            {
                playerCache[player] = DeadByMinecraftPlayer(player)
            }

            return playerCache[player] as DeadByMinecraftPlayer
        }
    }

    fun sendMessage(type: ChatMessageType, component: BaseComponent)
    {
        this.bukkit.spigot().sendMessage(type, component)
    }
}