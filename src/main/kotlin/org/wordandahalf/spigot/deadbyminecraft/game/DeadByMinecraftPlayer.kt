package org.wordandahalf.spigot.deadbyminecraft.game

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraftPlugin

class DeadByMinecraftPlayer private constructor(val bukkit: Player)
{
    companion object
    {
        private val playerCache : HashMap<Player, DeadByMinecraftPlayer> = hashMapOf()

        fun of(player: Player) : DeadByMinecraftPlayer
        {
            if(playerCache[player] !is DeadByMinecraftPlayer)
            {
                playerCache[player] = DeadByMinecraftPlayer(player)
            }

            return playerCache[player] as DeadByMinecraftPlayer
        }
    }

    fun getGame() : DeadByMinecraftGame?
    {
        val id = get("game_id", PersistentDataType.INTEGER)

        if(id is Int)
        {
            if(DeadByMinecraftGameManager.getGameByID(id) is DeadByMinecraftGame)
                return DeadByMinecraftGameManager.getGameByID(id)

            remove("game_id")
        }

        return null
    }

    fun <T, Z> get(key: String, type: PersistentDataType<T, Z>) : Z?
    {
        return this.bukkit.persistentDataContainer.get(NamespacedKey(DeadByMinecraftPlugin.Instance, key), type)
    }

    fun <T, Z> set(key: String, type: PersistentDataType<T, Z>, value: Z)
    {
        this.bukkit.persistentDataContainer.set(NamespacedKey(DeadByMinecraftPlugin.Instance, key), type, value)
    }

    fun remove(key: String)
    {
        this.bukkit.persistentDataContainer.remove(NamespacedKey(DeadByMinecraftPlugin.Instance, key))
    }

    fun <T, Z> has(key: String, type: PersistentDataType<T, Z>) : Boolean
    {
        return this.bukkit.persistentDataContainer.has(NamespacedKey(DeadByMinecraftPlugin.Instance, key), type)
    }
}