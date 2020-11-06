package org.wordandahalf.spigot.deadbyminecraft.worlds

import com.grinderwolf.swm.api.world.SlimeWorld
import org.bukkit.Bukkit
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
        bukkit.time = config.time
    }

    override fun dispose()
    {
        behavior.dispose()
    }
}