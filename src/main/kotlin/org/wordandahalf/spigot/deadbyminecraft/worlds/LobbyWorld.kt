package org.wordandahalf.spigot.deadbyminecraft.worlds

import com.grinderwolf.swm.api.world.SlimeWorld
import org.bukkit.Bukkit
import org.bukkit.World
import org.wordandahalf.spigot.deadbyminecraft.config.LobbyWorldConfig
import org.wordandahalf.spigot.deadbyminecraft.game.behaviors.Behavior
import org.wordandahalf.spigot.deadbyminecraft.game.behaviors.LobbyBehavior
import org.wordandahalf.spigot.deadbyminecraft.scheduling.Disposable
import java.security.InvalidParameterException

data class LobbyWorld(val world: SlimeWorld, val config: LobbyWorldConfig) : Disposable
{
    val bukkit : World
    val behavior : LobbyBehavior

    init
    {
        // Validate parameters
        val bukkitWorld = Bukkit.getWorld(world.name)
        if(bukkitWorld !is World)
            throw InvalidParameterException("World '${world.name}' is not loaded!")
        else
            bukkit = bukkitWorld

        val lobbyBehavior = Behavior.from(config, this)
        if(lobbyBehavior !is LobbyBehavior)
            throw InvalidParameterException("Could not create behavior '${config.behavior}'!")
        else
            behavior = lobbyBehavior

        // Initialize the world based on the provided configuration
        bukkit.time = config.time
    }

    override fun dispose()
    {
        behavior.dispose()
    }
}