package org.wordandahalf.spigot.deadbyminecraft.actions

import org.bukkit.World
import org.wordandahalf.spigot.deadbyminecraft.player.DeadByMinecraftPlayer
import org.wordandahalf.spigot.deadbyminecraft.player.roles.killer.KillerRole

class PlayerChooseKillerRoleAction(world: World, val player: DeadByMinecraftPlayer, val role: KillerRole) : Action(world)