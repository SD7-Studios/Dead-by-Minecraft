package org.wordandahalf.spigot.deadbyminecraft.game.worlds

import com.grinderwolf.swm.api.world.SlimeWorld
import org.bukkit.Bukkit
import org.wordandahalf.spigot.deadbyminecraft.config.GameWorldConfig

data class GameWorld(val world: SlimeWorld, val config: GameWorldConfig)
{
    val bukkit = Bukkit.getWorld(world.name)!!

    init
    {
        // Initialize the world based on the provided configuration
        bukkit.time = config.time
    }
}