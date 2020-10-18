package org.wordandahalf.spigot.deadbyminecraft.game.worlds

import com.grinderwolf.swm.api.world.SlimeWorld
import org.bukkit.Bukkit
import org.wordandahalf.spigot.deadbyminecraft.config.DeadByMinecraftConfig
import org.wordandahalf.spigot.deadbyminecraft.config.DeadByMinecraftLobbyWorldConfig

data class DeadByMinecraftLobbyWorld(val world: SlimeWorld, val config: DeadByMinecraftLobbyWorldConfig)
{
    val bukkit = Bukkit.getWorld(world.name)
}