package org.wordandahalf.spigot.deadbyminecraft.game.worlds

import com.grinderwolf.swm.api.world.SlimeWorld
import org.bukkit.Bukkit
import org.wordandahalf.spigot.deadbyminecraft.config.DeadByMinecraftConfig
import org.wordandahalf.spigot.deadbyminecraft.config.DeadByMinecraftGameWorldConfig

data class DeadByMinecraftGameWorld(val world: SlimeWorld, val config: DeadByMinecraftGameWorldConfig)
{
    val bukkit = Bukkit.getWorld(world.name)
}