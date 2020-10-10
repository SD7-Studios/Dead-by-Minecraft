package org.wordandahalf.spigot.deadbyminecraft

import com.grinderwolf.swm.api.SlimePlugin
import com.grinderwolf.swm.api.world.SlimeWorld
import com.grinderwolf.swm.api.world.properties.SlimeProperties
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap
import org.bukkit.Bukkit
import java.lang.Exception
import java.util.*

object DeadByMinecraftWorlds
{
    private const val LOBBY_WORLD_PREFIX = "dbm-lobby-world-"
    private const val GAME_WORLD_PREFIX = "dbm-game-world-"

    private lateinit var lobbyWorld : SlimeWorld
    private lateinit var gameWorld : SlimeWorld

    /**
     * Loads the world templates if they have not already been loaded
     * @return true if already loaded or loaded successfully, false otherwise
     */
    fun loadTemplates() : Boolean
    {
        try {
            // The SlimeWorldManager provides all of the methods for loading templates
            val slimePlugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin

            // The world templates are saved on disk (has various database support)
            val loader = slimePlugin.getLoader("file")

            // World properties
            val properties = SlimePropertyMap()
            properties.setString(SlimeProperties.DIFFICULTY, "peaceful")
            properties.setBoolean(SlimeProperties.ALLOW_ANIMALS, false)
            properties.setBoolean(SlimeProperties.ALLOW_MONSTERS, false)
            properties.setBoolean(SlimeProperties.PVP, false)
            properties.setString(SlimeProperties.WORLD_TYPE, "customized")

            // Load the lobby template, allow modification if debug mode is enabled
            lobbyWorld = slimePlugin.loadWorld(loader, DeadByMinecraftConfig.lobbyWorldName(), !DeadByMinecraft.DEBUG, properties)
            slimePlugin.generateWorld(lobbyWorld)

            // The game world has different properties
            properties.setString(SlimeProperties.DIFFICULTY, "hard")
            properties.setBoolean(SlimeProperties.PVP, false)

            // ""
            gameWorld = slimePlugin.loadWorld(loader, DeadByMinecraftConfig.gameWorldName(), !DeadByMinecraft.DEBUG, properties)
            slimePlugin.generateWorld(gameWorld)
        }
        catch (e: Exception)
        {
            DeadByMinecraft.Logger.warning(e.stackTraceToString())
            return false
        }

        return true
    }

    fun cloneLobby() : SlimeWorld
    {
        // Load a copy of the lobby template with a guaranteed random name
        val world = lobbyWorld.clone(LOBBY_WORLD_PREFIX + UUID.randomUUID().toString())
        (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin).generateWorld(world)

        // Set the time as is it is in the config
        Bukkit.getWorld(world.name)!!.time = DeadByMinecraftConfig.lobbyWorldTime().toLong()

        return world
    }

    fun cloneGame() : SlimeWorld
    {
        // Load a copy of the game template with a guaranteed random name
        val world = gameWorld.clone(GAME_WORLD_PREFIX + UUID.randomUUID().toString())
        (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin).generateWorld(world)

        // Set the time as is in the config
        Bukkit.getWorld(world.name)!!.time = DeadByMinecraftConfig.gameWorldTime().toLong()

        return world
    }
}