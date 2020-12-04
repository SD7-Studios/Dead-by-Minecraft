package org.wordandahalf.spigot.deadbyminecraft.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import org.wordandahalf.spigot.deadbyminecraft.config.Config
import org.wordandahalf.spigot.deadbyminecraft.game.Game
import org.wordandahalf.spigot.deadbyminecraft.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.game.items.ScriptableItemStack

class EventListener : Listener {
    @EventHandler
    /**
     * Handles resource pack and the edge case when a player is disconnected from a game without it ending
     */
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if(Config.Main.resourcePackUrl != "")
            e.player.setResourcePack(Config.Main.resourcePackUrl, Config.Main.resourcePackHash.chunked(2).map { it.toUpperCase().toInt(16).toByte() }.toByteArray())

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
        player.data.getGame()?.removePlayer(player)
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
            if(e.player.world == game.lobbyWorld.bukkit)
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