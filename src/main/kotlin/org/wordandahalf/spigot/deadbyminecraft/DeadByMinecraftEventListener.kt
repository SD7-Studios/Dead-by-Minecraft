package org.wordandahalf.spigot.deadbyminecraft

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftGame
import org.wordandahalf.spigot.deadbyminecraft.game.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.states.DeadByMinecraftLobbyState
import org.wordandahalf.spigot.deadbyminecraft.item.ScriptableItemStack

class DeadByMinecraftEventListener : Listener
{
    @EventHandler
    /**
     * Handles the edge case when a player is disconnected from a game without it ending
     */
    fun onPlayerJoin(e: PlayerJoinEvent)
    {
        val player = DeadByMinecraftPlayer.of(e.player)

        // TODO: Better system
        if(player.has("game_id", PersistentDataType.INTEGER))
        {
            val game = player.getGame()

            if(game is DeadByMinecraftGame)
            {
                game.addPlayer(player)
            }
            else
            {
                player.bukkit.sendMessage("The game you were previously in has ended!")
                // TODO: Remove all DBM tags
                player.remove("game_id")
            }
        }
    }

    @EventHandler
    /**
     * Prevents the player from moving if they are in the lobby
     */
    fun onPlayerMove(e: PlayerMoveEvent)
    {
        val game = DeadByMinecraftPlayer.of(e.player).getGame()
        if(game is DeadByMinecraftGame)
        {
            if(game.state is DeadByMinecraftLobbyState)
            {
                e.player.velocity = Vector().zero()
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    /**
     * Prevents the player from moving items around in their inventory
     */
    fun onInventoryClick(e: InventoryClickEvent)
    {
        if(e.whoClicked !is Player)
            return

        if(DeadByMinecraftPlayer.of(e.whoClicked as Player).getGame() is DeadByMinecraftGame)
        {
            e.isCancelled = true
        }
    }

    @EventHandler
    /**
     * Prevents the player from dropping item
     */
    fun onPlayerDropItem(e: PlayerDropItemEvent)
    {
        if(DeadByMinecraftPlayer.of(e.player).getGame() is DeadByMinecraftGame)
        {
            e.isCancelled = true
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteract(e: PlayerInteractEvent)
    {
        if(DeadByMinecraftPlayer.of(e.player).getGame() is DeadByMinecraftGame)
        {
            e.isCancelled = true
            return
        }

        if(e.item is ItemStack) {
            ScriptableItemStack.getExecutor(e.item as ItemStack)?.accept(e, e.item as ItemStack)
        }
    }
}