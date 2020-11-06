package org.wordandahalf.spigot.deadbyminecraft.game.behaviors

import ninja.leaping.configurate.reactive.Disposable
import org.bukkit.World
import org.wordandahalf.spigot.deadbyminecraft.DeadByMinecraft
import org.wordandahalf.spigot.deadbyminecraft.actions.Actions
import org.wordandahalf.spigot.deadbyminecraft.config.GameWorldConfig
import org.wordandahalf.spigot.deadbyminecraft.config.LobbyWorldConfig
import org.wordandahalf.spigot.deadbyminecraft.worlds.GameWorld
import org.wordandahalf.spigot.deadbyminecraft.worlds.LobbyWorld

/**
 * Abstract superclass that provides logic for lobby and game worlds.
 * It implements the behavior by keeping track of state and listening to events.
 */
abstract class Behavior(val world: World) : Disposable
{
    companion object
    {
        // Holds registered Behavior classes assigned to the name of their class
        private val registry = hashMapOf(
            Pair(DefaultGameBehavior::class.java.simpleName, DefaultGameBehavior::class.java),
            Pair(DefaultLobbyBehavior::class.java.simpleName, DefaultLobbyBehavior::class.java)
        )

        /**
         * Constructs a [LobbyBehavior] from a [LobbyWorldConfig] and a [LobbyWorld].
         *
         * If [LobbyWorldConfig.behavior] does not contain the name of a registered [LobbyBehavior], it returns null.
         */
        fun from(config: LobbyWorldConfig, world: LobbyWorld) : LobbyBehavior?
        {
            val behaviorName = config.behavior

            // Ensure that the behavior is registered
            if(registry[behaviorName] == null)
            {
                DeadByMinecraft.Logger.severe("The behavior '${config.behavior}' for the lobby world '${world.bukkit.name}' is unregistered!")
                return null
            }

            // Ensure that the behavior is a LobbyBehavior
            return if(registry[behaviorName]!!.superclass == LobbyBehavior::class.java)
            {
                registry[behaviorName]!!.getConstructor(LobbyWorldConfig::class.java, LobbyWorld::class.java).newInstance(config, world) as LobbyBehavior
            }
            else
            {
                DeadByMinecraft.Logger.severe("The behavior '${config.behavior}' for the lobby world '${world.bukkit.name}' is not a LobbyBehavior!!")
                null
            }
        }

        /**
         * Constructs a [GameBehavior] from a [GameWorldConfig] and a [GameWorld].
         *
         * If [GameWorldConfig.behavior] does not contain the name of a registered [GameBehavior], it returns null.
         */
        fun from(config: GameWorldConfig, world: GameWorld) : GameBehavior?
        {
            val behaviorName = config.behavior

            // Ensure that the behavior is registered
            if(registry[behaviorName] == null)
            {
                DeadByMinecraft.Logger.severe("The behavior '${config.behavior}' for the game world '${world.bukkit.name}' is unregistered!")
                return null
            }

            // Ensure that the behavior is a GameBehavior
            if(registry[behaviorName]!!.superclass == GameBehavior::class.java)
            {
                return registry[behaviorName]!!.getConstructor(GameWorldConfig::class.java, GameWorld::class.java).newInstance(config, world) as GameBehavior
            }
            else
            {
                DeadByMinecraft.Logger.severe("The behavior '${config.behavior}' for the game world '${world.bukkit.name}' is not a GameBehavior!")
                return null
            }
        }

        /**
         * Registers [clazz] so that [LobbyWorldConfig] and [GameWorldConfig] can use it as their behavior
         */
        fun register(clazz: Class<out Behavior>)
        {
            registry.putIfAbsent(clazz::class.java.simpleName, clazz)
        }
    }

    init
    {
        Actions.register(this)
        // Actions.submit(PlayerJoinAction(lobbyWorld, player))
        // Actions.submit(PlayerLeaveAction(player.world, player))
        // -> will submit action to the behavior registered with lobbyWorld
        // Register this instance with the EventManager for event distribution
    }
}