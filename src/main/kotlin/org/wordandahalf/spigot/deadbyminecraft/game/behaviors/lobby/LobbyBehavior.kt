package org.wordandahalf.spigot.deadbyminecraft.game.behaviors.lobby

import org.wordandahalf.spigot.deadbyminecraft.config.LobbyWorldConfig
import org.wordandahalf.spigot.deadbyminecraft.game.behaviors.Behavior
import org.wordandahalf.spigot.deadbyminecraft.worlds.LobbyWorld

abstract class LobbyBehavior(protected val config: LobbyWorldConfig, world: LobbyWorld) : Behavior(world.bukkit)