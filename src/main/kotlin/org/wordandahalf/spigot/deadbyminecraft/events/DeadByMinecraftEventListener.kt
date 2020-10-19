package org.wordandahalf.spigot.deadbyminecraft.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import org.wordandahalf.spigot.deadbyminecraft.game.Game
import org.wordandahalf.spigot.deadbyminecraft.game.GameManager
import org.wordandahalf.spigot.deadbyminecraft.game.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.states.LobbyState
import org.wordandahalf.spigot.deadbyminecraft.game.items.ScriptableItemStack

class DeadByMinecraftEventListener : Listener {
    @EventHandler
            /**
             * Handles the edge case when a player is disconnected from a game without it ending
             */
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = DeadByMinecraftPlayer.of(e.player)

        if (player.data.gameID is Int) {
            val game = player.data.getGame()
            if (game is Game) {
                game.addPlayer(player)
            } else {
                player.bukkit.sendMessage("The game you were previously in has ended!")
                player.data.gameID = null
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent)
    {
        val player = DeadByMinecraftPlayer.of(e.player)
        GameManager.byPlayer(player)?.removePlayer(player)
        DeadByMinecraftPlayer.remove(e.player)
    }

    @EventHandler
    /**
     * Prevents the player from moving if they are in the lobby
     */
    fun onPlayerMove(e: PlayerMoveEvent)
    {
        val game = DeadByMinecraftPlayer.of(e.player).data.getGame()
        if(game is Game)
        {
            if(game.state is LobbyState)
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

        if(DeadByMinecraftPlayer.of(e.whoClicked as Player).data.getGame() is Game)
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
        if(DeadByMinecraftPlayer.of(e.player).data.getGame() is Game)
        {
            e.isCancelled = true
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteract(e: PlayerInteractEvent)
    {
        if(e.item is ItemStack && ScriptableItemStack.isScriptableItemStack(e.item!!)) {
            ScriptableItemStack.getExecutor(e.item as ItemStack)?.accept(e, e.item as ItemStack)
        }

        if(DeadByMinecraftPlayer.of(e.player).data.getGame() is Game)
        {
            e.isCancelled = true
            return
        }
    }
}