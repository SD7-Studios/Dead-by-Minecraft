package org.wordandahalf.spigot.deadbyminecraft.game.behaviors

import org.wordandahalf.spigot.deadbyminecraft.config.GameWorldConfig
import org.wordandahalf.spigot.deadbyminecraft.worlds.GameWorld

abstract class GameBehavior(protected val config: GameWorldConfig, game: GameWorld) : Behavior(game.bukkit)