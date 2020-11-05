package org.wordandahalf.spigot.deadbyminecraft.game.worlds

import com.grinderwolf.swm.api.world.SlimeWorld
import org.bukkit.Bukkit
import org.wordandahalf.spigot.deadbyminecraft.config.LobbyWorldConfig

data class LobbyWorld(val world: SlimeWorld, val config: LobbyWorldConfig)
{
    val bukkit = Bukkit.getWorld(world.name)!!

    init
    {
        // Initialize the world based on the provided configuration
        bukkit.time = config.time
    }
}