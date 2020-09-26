package org.wordandahalf.spigot.deadbyminecraft.game

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.game.role.DeadByMinecraftRole
import org.wordandahalf.spigot.deadbyminecraft.persistence.DeadByMinecraftPlayerDataDataType

/**
 * Wrapper for the Bukkit Player class, providing methods for saving and deleting DBM-related data.
 */
class DeadByMinecraftPlayer private constructor(val bukkit: Player)
{
    val data = Data()

    /**
     * Object for representing persistent data of a player
     *
     * TODO: Pretty sure #save() must be called in order to save the data in the object to its respective player
     */
    class Data
    {
        var gameID : Int? = null
        var role : DeadByMinecraftRole? = null

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

    fun sendMessage(type: ChatMessageType, vararg components: BaseComponent)
    {
        this.bukkit.spigot().sendMessage(type, *components)
    }

    /**
     * Saves the data stored in the object to the player's NBT data
     */
    fun saveData()
    {
        bukkit.persistentDataContainer.set(NamespacedKey(DeadByMinecraft.instance, "data"), DeadByMinecraftPlayerDataDataType.TYPE, data)
    }

    /**
     * Deletes all DeadByMinecraft data stored in the player's NBT
     */
    fun deleteData()
    {
        bukkit.persistentDataContainer.remove(NamespacedKey(DeadByMinecraft.instance, "data"))
    }
}