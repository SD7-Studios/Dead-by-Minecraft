package org.wordandahalf.spigot.deadbyminecraft.game.behaviors.game

import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.actions.ActionHandler
import org.wordandahalf.spigot.deadbyminecraft.actions.PlayerJoinAction
import org.wordandahalf.spigot.deadbyminecraft.actions.PlayerLeaveAction
import org.wordandahalf.spigot.deadbyminecraft.config.GameWorldConfig
import org.wordandahalf.spigot.deadbyminecraft.worlds.GameWorld

class DefaultGameBehavior(config: GameWorldConfig, world: GameWorld) : GameBehavior(config, world)
{
    init
    {
        DeadByMinecraft.Logger.info("A ${DefaultGameBehavior::class.java.simpleName} has been constructed for world '${world.bukkit.name}'")
    }

    @ActionHandler
    fun onPlayerJoin(action: PlayerJoinAction)
    {
        val player = action.player
        player.bukkit.teleport(world.spawnLocation)
    }

    @ActionHandler
    fun onPlayerLeave(action: PlayerLeaveAction)
    {
        action.player.bukkit.sendMessage("You left a game!")
    }
}