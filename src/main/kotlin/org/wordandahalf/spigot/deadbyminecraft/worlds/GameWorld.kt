package org.wordandahalf.spigot.deadbyminecraft.worlds

import com.grinderwolf.swm.api.world.SlimeWorld
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.World
import org.wordandahalf.spigot.deadbyminecraft.config.GameWorldConfig
import org.wordandahalf.spigot.deadbyminecraft.game.behaviors.Behavior
import org.wordandahalf.spigot.deadbyminecraft.game.behaviors.GameBehavior
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Disposable
import java.security.InvalidParameterException

data class GameWorld(val world: SlimeWorld, val config: GameWorldConfig) : Disposable
{
    val bukkit : World
    val behavior : GameBehavior

    init
    {
        val bukkitWorld = Bukkit.getWorld(world.name)
        if(bukkitWorld !is World)
            throw InvalidParameterException("World '${world.name}' is not loaded!")
        else
            bukkit = bukkitWorld

        val gameBehavior = Behavior.from(config, this)
        if(gameBehavior !is GameBehavior)
            throw InvalidParameterException("Could not instantiate behavior '${config.behavior}'!")
        else
            behavior = gameBehavior

        // Initialize the world based on the provided configuration

        bukkit.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        bukkit.setGameRule(GameRule.DISABLE_RAIDS, true)
        bukkit.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        bukkit.setGameRule(GameRule.DO_FIRE_TICK, false)
        bukkit.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
        bukkit.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        bukkit.setGameRule(GameRule.DO_PATROL_SPAWNING, false)
        bukkit.setGameRule(GameRule.DO_TRADER_SPAWNING, false)
        bukkit.setGameRule(GameRule.KEEP_INVENTORY, true)
        bukkit.setGameRule(GameRule.NATURAL_REGENERATION, false)
        bukkit.setGameRule(GameRule.REDUCED_DEBUG_INFO, true)
        bukkit.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)

        bukkit.time = config.time
    }

    override fun dispose()
    {
        behavior.dispose()
    }
}